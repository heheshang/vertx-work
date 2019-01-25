package com.vertx.kue.queue;

import com.vertx.kue.Kue;
import com.vertx.kue.util.RedisHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;

import java.util.Optional;


/**
 * The verticle for processing Kue tasks.
 *
 * @author Eric Zhao
 */
public class KueWorker extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Job.class);

    private final Kue kue;

    /**
     * Every worker use different clients.
     */
    private RedisClient client;

    private EventBus eventBus;

    private Job job;

    private final String type;

    private final Handler<Job> jobHandler;

    /**
     * Preserve for unregister the consumer.
     */
    private MessageConsumer doneConsumer;

    private MessageConsumer doneFailConsumer;

    public KueWorker(String type, Handler<Job> jobHandler, Kue kue) {

        this.type = type;
        this.jobHandler = jobHandler;
        this.kue = kue;
    }

    @Override
    public void start() throws Exception {

        this.eventBus = vertx.eventBus();
        this.client = RedisHelper.client(vertx, config());

        prepareAndStart();
    }

    /**
     * Prepare job and start processing procedure.
     * 准备要处理的任务并且开始处理任务的过程
     */
    private void prepareAndStart() {

        cleanup();
        this.getJobFromBackend().setHandler(jr -> {
            if (jr.succeeded()) {
                if (jr.result().isPresent()) {
                    this.job = jr.result().get();
                    process();
                } else {
                    this.emitJobEvent("error", null, new JsonObject().put("message", "job_not_exist"));
                    throw new IllegalStateException("job not exist");
                }
            } else {
                this.emitJobEvent("error", null, new JsonObject().put("message", jr.cause().getMessage()));
                jr.cause().printStackTrace();
            }
        });
    }

    /**
     * Process the job.
     */
    private void process() {

        long curTime = System.currentTimeMillis();
        this.job.setStarted_at(curTime)
                .set("started_at", String.valueOf(curTime))
                .compose(Job::active)
                .setHandler(r -> {
                    if (r.succeeded()) {
                        Job j = r.result();
                        // emit start event
                        this.emitJobEvent("start", j, null);

                        logger.debug("KueWorker::process[instance:Verticle(" + this.deploymentID() + ")] with job " + job.getId());
                        // process logic invocation
                        try {
                            jobHandler.handle(j);
                        } catch (Exception ex) {
                            j.done(ex);
                        }
                        // subscribe the job done event

                        doneConsumer = eventBus.consumer(Kue.workerAddress("done", j), msg -> {
                            createDoneCallback(j).handle(Future.succeededFuture(
                                    ((JsonObject) msg.body()).getJsonObject("result")));
                        });
                        doneFailConsumer = eventBus.consumer(Kue.workerAddress("done_fail", j), msg -> {
                            createDoneCallback(j).handle(Future.failedFuture(
                                    (String) msg.body()));
                        });
                    } else {
                        this.emitJobEvent("error", this.job, new JsonObject().put("message", r.cause().getMessage()));
                        r.cause().printStackTrace();
                    }
                });
    }

    private void cleanup() {

        Optional.ofNullable(doneConsumer).ifPresent(MessageConsumer::unregister);
        Optional.ofNullable(doneFailConsumer).ifPresent(MessageConsumer::unregister);
        this.job = null;
    }

    private void error(Throwable ex, Job job) {

        JsonObject err = new JsonObject().put("message", ex.getMessage())
                .put("id", job.getId());
        eventBus.send(Kue.workerAddress("error"), err);
    }

    private void fail(Throwable ex) {

        job.failedAttempt(ex).setHandler(r -> {
            if (r.failed()) {
                this.error(r.cause(), job);
            } else {
                Job res = r.result();
                if (res.hasAttempts()) {
                    this.emitJobEvent("failed_attempt", job, new JsonObject().put("message", ex.getMessage())); // shouldn't include err?
                } else {
                    this.emitJobEvent("failed", job, new JsonObject().put("message", ex.getMessage()));
                }
                prepareAndStart();
            }
        });
    }

    /**
     * Redis zpop atomic primitive with transaction.
     * zpop命令是一个原子操作，用于从有序集合中弹出最小score值的元素
     * @param key redis key
     * @return the async result of zpop
     */
    private Future<Long> zpop(String key) {

        Future<Long> future = Future.future();
        client.transaction()
                .multi(_failure())
                .zrange(key, 0, 0, _failure())
                .zremrangebyrank(key, 0, 0, _failure())
                .exec(r -> {
                    if (r.succeeded()) {
                        JsonArray res = r.result();
                        // empty set
                        if (res.getJsonArray(0).size() == 0)
                            future.fail(new IllegalStateException("Empty zpop set"));
                        else {
                            try {
                                future.complete(Long.parseLong(RedisHelper.stripFIFO(
                                        res.getJsonArray(0).getString(0))));
                            } catch (Exception ex) {
                                future.fail(ex);
                            }
                        }
                    } else {
                        future.fail(r.cause());
                    }
                });
        return future;
    }

    /**
     * Get a job from Redis backend by priority.
     * Redis中按照优先级顺序获取任务实体
     * @return async result of job
     */
    private Future<Optional<Job>> getJobFromBackend() {

        Future<Optional<Job>> future = Future.future();
        client.blpop(RedisHelper.getKey(this.type + ":jobs"), 0, r1 -> {
            if (r1.failed()) {
                client.lpush(RedisHelper.getKey(this.type + ":jobs"), "1", r2 -> {
                    if (r2.failed())
                        future.fail(r2.cause());
                });
            } else {
                this.zpop(RedisHelper.getKey("jobs:" + this.type + ":INACTIVE"))
                        .compose(kue::getJob)
                        .setHandler(r -> {
                            if (r.succeeded()) {
                                future.complete(r.result());
                            } else
                                future.fail(r.cause());
                        });
            }
        });
        return future;
    }

    /**
     * 任务处理有两种情况：完成和失败，因此我们先来看任务成功处理的情况。我们首先给任务的用时(duration)赋值 (2)，
     * 并且如果任务产生了结果，也给结果(result)赋值 (3)。
     * 然后我们调用job.complete方法将状态设置为COMPLETE (4)。
     * 如果成功的话，我们就检查removeOnComplete标志位 (5) 并决定是否将任务从Redis中移除。
     * 然后我们向Event Bus发送任务完成事件(complete)以及队列事件job_complete (6)。
     * 现在这个任务的处理过程已经结束了，worker需要准备处理下一个任务了，因此最后我们调用prepareAndStart方法准备处理下一个Job。
     * @param job
     * @return
     */
    private Handler<AsyncResult<JsonObject>> createDoneCallback(Job job) {

        return r0 -> {
            if (job == null) {
                // maybe should warn
                return;
            }
            if (r0.failed()) {
                //任务处理过程中很可能会遇见各种各样的问题而失败。当任务处理失败时，我们调用KueWorker中的fail方法
                this.fail(r0.cause());
                return;
            }
            long dur = System.currentTimeMillis() - job.getStarted_at();
            job.setDuration(dur)
                    .set("duration", String.valueOf(dur));
            JsonObject result = r0.result();
            if (result != null) {
                job.setResult(result)
                        .set("result", result.encodePrettily());
            }

            job.complete().setHandler(r -> {
                if (r.succeeded()) {
                    Job j = r.result();
                    if (j.isRemoveOnComplete()) {
                        j.remove();
                    }
                    this.emitJobEvent("complete", j, null);

                    this.prepareAndStart(); // prepare for next job
                }
            });
        };
    }

    @Override
    public void stop() throws Exception {
        // stop hook
        cleanup();
    }

    /**
     * Emit job event.
     * 执行
     * @param event event type
     * @param job   corresponding job
     * @param extra extra data
     */
    private void emitJobEvent(String event, Job job, JsonObject extra) {

        JsonObject data = new JsonObject().put("extra", extra);
        if (job != null) {
            data.put("job", job.toJson());
        }
        eventBus.send(Kue.workerAddress("job_" + event), data);
        switch (event) {
            case "failed":
            case "failed_attempt":
                eventBus.send(Kue.getCertainJobAddress(event, job), data);
                break;
            case "error":
                eventBus.send(Kue.workerAddress("error"), data);
                break;
            default:
                eventBus.send(Kue.getCertainJobAddress(event, job), job.toJson());
        }
    }

    private static <T> Handler<AsyncResult<T>> _failure() {

        return r -> {
            if (r.failed())
                r.cause().printStackTrace();
        };
    }

}
