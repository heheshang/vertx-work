package com.vertx.kue.queue;

import com.vertx.kue.service.JobService;
import com.vertx.kue.util.RedisHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.serviceproxy.ServiceBinder;


/**
 * Vert.x Blueprint - Job Queue
 * Kue Verticle
 *
 * @author Eric Zhao
 */
public class KueVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(Job.class);

    public static final String EB_JOB_SERVICE_ADDRESS = "vertx.kue.service.job.internal";

    private JsonObject config;

    private JobService jobService;

    @Override
    public void start(Future<Void> future) throws Exception {

        this.config = config();
        this.jobService = JobService.create(vertx, config);
        // create redis client
        RedisClient redisClient = RedisHelper.client(vertx, config);
        // test connection
        redisClient.ping(pr -> {
            if (pr.succeeded()) {
                logger.info("Kue Verticle is running...");

                // register job service
                new ServiceBinder(vertx).setAddress(EB_JOB_SERVICE_ADDRESS).register(JobService.class, jobService);
//        ProxyHelper.registerService(JobService.class, vertx, jobService, EB_JOB_SERVICE_ADDRESS);

                future.complete();
            } else {
                logger.error("oops!", pr.cause());
                future.fail(pr.cause());
            }
        });
    }

}
