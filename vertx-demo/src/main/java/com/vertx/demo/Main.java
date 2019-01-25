package com.vertx.demo;

import com.vertx.demo.verticle.TodoVerticle;
import io.vertx.core.Vertx;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-25-上午 9:09
 */
public class Main {

    public static void main(String[] args) throws Exception {

        TodoVerticle verticle = new TodoVerticle();
        Vertx.vertx().deployVerticle(verticle);
    }
}
