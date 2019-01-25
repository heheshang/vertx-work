package com.vertx.kue.service;

import com.vertx.kue.queue.Job;
import com.vertx.kue.queue.JobState;
import com.vertx.kue.service.impl.JobServiceImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import java.util.List;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-25-上午 11:05
 */
@ProxyGen
@VertxGen
public interface JobService {

    /**
     * Factory method for creating a {@link JobService} instance.
     *
     * @param vertx  Vertx instance
     * @param config configuration
     * @return the new {@link JobService} instance
     */
    static JobService create(Vertx vertx, JsonObject config) {

        return new JobServiceImpl(vertx, config);
    }

    /**
     * Factory method for creating a {@link JobService} service proxy.
     * This is useful for doing RPCs.
     *
     * @param vertx   Vertx instance
     * @param address event bus address of RPC
     * @return the new {@link JobService} service proxy
     */
    static JobService createProxy(Vertx vertx, String address) {

        return new ServiceProxyBuilder(vertx).setAddress(address).build(JobService.class);
    }

    /**
     * Get the certain from backend by id.
     *  获取任务的方法非常简单。直接利用hgetall命令从Redis中取出对应的任务即可
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService getJob(long id, Handler<AsyncResult<Job>> handler);

    /**
     * Remove a job by id.
     *  我们可以将此方法看作是getJob和Job#remove两个方法的组合
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService removeJob(long id, Handler<AsyncResult<Void>> handler);

    /**
     * Judge whether a job with certain id exists.
     *  使用exists命令判断对应id的任务是否存在
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService existsJob(long id, Handler<AsyncResult<Boolean>> handler);

    /**
     * Get job log by id.
     * 使用lrange命令从vertx_kue:job:{id}:log列表中取出日志。
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService getJobLog(long id, Handler<AsyncResult<JsonArray>> handler);

    /**
     * Get a list of job in certain state in range (from, to) with order.
     *  指定状态，对应的key为vertx_kue:jobs:{state}。
     * @param state   expected job state
     * @param from    from
     * @param to      to
     * @param order   range order
     * @param handler async result handler
     */
    @Fluent
    JobService jobRangeByState(String state, long from, long to, String order, Handler<AsyncResult<List<Job>>> handler);

    /**
     * Get a list of job in certain state and type in range (from, to) with order.
     *  指定状态和类型，对应的key为vertx_kue:jobs:{type}:{state}。
     * @param type    expected job type
     * @param state   expected job state
     * @param from    from
     * @param to      to
     * @param order   range order
     * @param handler async result handler
     */
    @Fluent
    JobService jobRangeByType(String type, String state, long from, long to, String order, Handler<AsyncResult<List<Job>>> handler);

    /**
     * Get a list of job in range (from, to) with order.
     * 对应的key为vertx_kue:jobs。
     * @param from    from
     * @param to      to
     * @param order   range order
     * @param handler async result handler
     */
    @Fluent
    JobService jobRange(long from, long to, String order, Handler<AsyncResult<List<Job>>> handler);

    // Runtime cardinality metrics

    /**
     * Get cardinality by job type and state.
     * 利用zcard命令获取某一指定状态和类型下任务的数量。
     * @param type    job type
     * @param state   job state
     * @param handler async result handler
     */
    @Fluent
    JobService cardByType(String type, JobState state, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality by job state.
     *利用zcard命令获取某一指定状态下任务的数量。
     * @param state   job state
     * @param handler async result handler
     */
    @Fluent
    JobService card(JobState state, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of completed jobs.
     *
     * @param type    job type; if null, then return global metrics
     * @param handler async result handler
     */
    @Fluent
    JobService completeCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of failed jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService failedCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of inactive jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService inactiveCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of active jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService activeCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of delayed jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService delayedCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get the job types present.
     * 利用smembers命令获取vertx_kue:job:types集合中存储的所有的任务类型。
     * @param handler async result handler
     */
    @Fluent
    JobService getAllTypes(Handler<AsyncResult<List<String>>> handler);

    /**
     * Return job ids with the given {@link JobState}.
     * 使用zrange获取某一指定状态下所有任务的ID。
     * @param state   job state
     * @param handler async result handler
     */
    @Fluent
    JobService getIdsByState(JobState state, Handler<AsyncResult<List<Long>>> handler);

    /**
     * Get queue work time in milliseconds.
     * 使用get命令从vertx_kue:stats:work-time中获取Vert.x Kue的工作时间。
     * @param handler async result handler
     */
    @Fluent
    JobService getWorkTime(Handler<AsyncResult<Long>> handler);
}
