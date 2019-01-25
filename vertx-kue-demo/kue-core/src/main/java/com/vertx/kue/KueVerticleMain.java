package com.vertx.kue;

import com.vertx.kue.queue.KueVerticle;
import io.vertx.core.Vertx;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-25-下午 4:01
 */
public class KueVerticleMain {

    public static void main(String[] args) {

        KueVerticle verticle = new KueVerticle();
        Vertx.vertx().deployVerticle(verticle);

    }
}
