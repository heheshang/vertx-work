package com.vertx.demo.service.impl;

import com.vertx.demo.Todo;
import com.vertx.demo.constants.Constants;
import com.vertx.demo.service.TodoService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-24-上午 11:43
 */
public class RedisTodoService implements TodoService {


    private final Vertx vertx;

    private final RedisOptions config;

    private final RedisClient redis;

    public RedisTodoService(Vertx vertx, RedisOptions config) {

        this.vertx = vertx;
        this.config = config;
        this.redis = RedisClient.create(vertx, config);
        initData();
    }

    @Override
    public Future<Boolean> initData() {

        Todo sample = new Todo(Math.abs(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)),
                "Something to do...", false, 1, "todo/ex");
        return this.insert(sample);
    }

    @Override
    public Future<Boolean> insert(Todo todo) {

        final Future<Boolean> result = Future.future();
        final String encoded = Json.encode(todo);

        this.redis.hset(Constants.REDIS_TODO_KEY, String.valueOf(todo.getId()), encoded, res -> {
            if (res.succeeded()) {
                result.complete(true);
            } else {
                result.fail(res.cause());
            }
        });

        return result;
    }

    @Override
    public Future<List<Todo>> getAll() {

        Future<List<Todo>> result = Future.future();

        redis.hvals(Constants.REDIS_TODO_KEY, res -> {
            if (res.succeeded()) {
                result.complete(res.result().stream()
                        .map(x -> Json.decodeValue((String) x, Todo.class))
                        .collect(Collectors.toList()));
            } else {
                result.fail(res.cause());
            }
        });
        return result;
    }

    @Override
    public Future<Optional<Todo>> getCertain(String todoID) {

        Future<Optional<Todo>> result = Future.future();
        this.redis.hget(Constants.REDIS_TODO_KEY, todoID, res -> {
            if (res.succeeded()) {
                Optional<Todo> todo = Optional.of(Json.decodeValue(res.result(), Todo.class));
                result.complete(todo);
            } else {
                result.fail(res.cause());
            }
        });
        return result;
    }

    @Override
    public Future<Todo> update(String todoId, Todo newTodo) {
/*
        return this.getCertain(todoId).compose(old -> {
            if (old.isPresent()) {
                Todo fnTodo = old.get().merge(newTodo);
                return this.insert(fnTodo)
                        .map(r -> r ? fnTodo : null);
            } else {
                return Future.succeededFuture();
            }
        });*/

        Future<Todo> result = Future.future();
        redis.hget(Constants.REDIS_TODO_KEY, todoId, x -> {
            if (x.succeeded()) {
                String res = x.result();
                if (res == null) {
                    result.complete(null);
                } else {
                    Todo oldTodo = Json.decodeValue(res, Todo.class);
                    Todo fnTodo = oldTodo.merge(newTodo);

                    String fnTodoStr = Json.encodePrettily(fnTodo);

                    redis.hset(Constants.REDIS_TODO_KEY, todoId, fnTodoStr, r -> {
                        if (r.succeeded()) {
                            result.complete(fnTodo);
                        } else {
                            result.fail(r.cause());
                        }

                    });
                }
            } else {
                result.fail(x.cause());
            }
        });


        return result;
    }

    @Override
    public Future<Boolean> delete(String todoId) {

        Future<Boolean> result = Future.future();

        redis.hdel(Constants.REDIS_TODO_KEY, todoId, res -> {
            if (res.succeeded()) {
                result.complete(true);
            } else {
                result.fail(res.cause());
            }
        });

        return result;
    }

    @Override
    public Future<Boolean> deleteAll() {

        Future<Boolean> result = Future.future();

        redis.del(Constants.REDIS_TODO_KEY, res -> {
            if (res.succeeded()) {
                result.complete(true);
            } else {
                result.fail(res.cause());
            }
        });

        return result;
    }
}
