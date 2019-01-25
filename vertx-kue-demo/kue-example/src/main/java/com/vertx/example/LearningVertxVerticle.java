package com.vertx.example;

import com.vertx.kue.Kue;
import com.vertx.kue.queue.Job;
import com.vertx.kue.queue.Priority;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-25-下午 3:46
 */
public class LearningVertxVerticle extends AbstractVerticle {




    @Override
    public void start() throws Exception {
        // 创建工作队列
        Kue kue = Kue.createQueue(vertx, config());

        //监听全局错误
        kue.on("error", message -> {
            System.out.println("[Global Error] " + message.body());
        });


        JsonObject data = new JsonObject()
                .put("title", "Learning Vert.x")
                .put("content", "core");

        Job j = kue.createJob("learn vertx", data)
                .priority(Priority.HIGH)
                .onComplete(r -> {
                    System.out.println("Feeling: " + r.getResult().getString("feeling", "none"));
                }).onFailure(r -> {
                    System.out.println("eee...so difficult...");
                }).onProgress(r -> {
                    System.out.println("I love this! My progress => " + r);
                });

        j.save().setHandler(r0 -> {
            if (r0.succeeded()) {
                kue.processBlocking("learn vertx", 1, job -> {
                    job.progress(10, 100);
                    vertx.setTimer(3000, r1 -> {
                        job.setResult(new JsonObject().put("feeling", "amazing and wonderful!"))
                                .done();
                    });
                });
            } else {
                System.err.println("Wow, something happened: " + r0.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {

        LearningVertxVerticle verticle = new LearningVertxVerticle();
        Vertx.vertx().deployVerticle(verticle);
    }
}
