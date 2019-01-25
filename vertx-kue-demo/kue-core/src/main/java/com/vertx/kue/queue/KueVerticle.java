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
 *
 * 如何利用Service Proxy将服务注册至Event Bus上。
 * 这里我们还需要一个KueVerticle来创建要注册的服务实例，并且将其注册至Event Bus上。
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
                /**
                 * 在start方法中，我们创建了一个任务服务实例 (2)，然后通过ping命令测试Redis连接 (3)。如果连接正常，那么我们就可以通过ProxyHelper类中的registerService辅助方法来将服务实例注册至Event Bus上 (4)。
                 *
                 * 这样，一旦我们在集群模式下部署KueVerticle，服务就会被发布至Event Bus上，然后我们就可以在其他组件中去远程调用此服务了。很奇妙吧
                 */
                // register job service
                new ServiceBinder(vertx).setAddress(EB_JOB_SERVICE_ADDRESS).register(JobService.class, jobService);
                // ProxyHelper.registerService(JobService.class, vertx, jobService, EB_JOB_SERVICE_ADDRESS);

                future.complete();
            } else {
                logger.error("oops!", pr.cause());
                future.fail(pr.cause());
            }
        });
    }

}
