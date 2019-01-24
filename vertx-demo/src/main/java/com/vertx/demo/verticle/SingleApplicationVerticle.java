package com.vertx.demo.verticle;

import com.vertx.demo.Todo;
import com.vertx.demo.constants.Constants;
import com.vertx.demo.service.TodoService;
import com.vertx.demo.service.impl.JdbcTodoService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-24-上午 10:19
 */
@Slf4j
public class SingleApplicationVerticle extends AbstractVerticle {

    private static final String Host_host = "0.0.0.0";

    private static final String redis_host = "127.0.0.1";

    private static final int http_port = 8082;

    private static final int redis_port = 6379;

    private RedisClient redisClient;

    private TodoService todoService = new JdbcTodoService(vertx,config());

    @Override
    public void start(Future<Void> future) throws Exception {

        this.initData();

        Router router = Router.router(vertx);

        Set<String> allowHeaders = new HashSet<>();

        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods)
        );

        router.route().handler(BodyHandler.create());
        // Todo :routes

        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.post(Constants.API_CREATE).handler(this::handleCreateTodo);
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);


        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(http_port, Host_host, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    private void initData() {

        RedisOptions config = new RedisOptions()
                .setHost(config().getString("redis.host", redis_host))
                .setPort(config().getInteger("redis.host", redis_port));

        this.redisClient = RedisClient.create(vertx, config);
        // test connection
        redisClient.hset(Constants.REDIS_TODO_KEY, "24", Json.encodePrettily(
                new Todo(24, "Something to do...", false, 1, "todo/ex")), res -> {
            if (res.failed()) {
                log.error("Redis service is not running!");
                res.cause().printStackTrace();
            }
        });
    }

    private void handleGetTodo(RoutingContext context) {
        // (1)
        String todoID = context.request().getParam("todoId");
        if (todoID == null) {
            // (2)
            sendError(400, context.response());
        } else {
            // (3)
            redisClient.hget(Constants.REDIS_TODO_KEY, todoID, x -> {
                if (x.succeeded()) {
                    String result = x.result();
                    if (result == null)
                        sendError(404, context.response());
                    else {
                        // (4)
                        context.response()
                                .putHeader("content-type", "application/json")
                                .end(result);
                    }
                } else {
                    sendError(503, context.response());
                }
            });
        }
    }

    private void handleGetAll(RoutingContext context) {

        redisClient.hvals(Constants.REDIS_TODO_KEY, res -> { // (1)
            if (res.succeeded()) {
                String encoded = Json.encodePrettily(res.result().stream() // (2)
                        .map(x -> new Todo((String) x))
                        .collect(Collectors.toList()));
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded); // (3)
            } else {
                sendError(503, context.response());
            }
        });
    }

    private void handleCreateTodo(RoutingContext context) {

        try {
            final Todo todo = wrapObject(new Todo(context.getBodyAsString()), context);
            final String encoded = Json.encodePrettily(todo);
            redisClient.hset(Constants.REDIS_TODO_KEY, String.valueOf(todo.getId()),
                    encoded, res -> {
                        if (res.succeeded())
                            context.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        else
                            sendError(503, context.response());
                    });
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private Todo wrapObject(Todo todo, RoutingContext context) {

        int id = todo.getId();
        if (id > Todo.getIncId()) {
            Todo.setIncIdWith(id);
        } else if (id == 0)
            todo.setIncId();
        todo.setUrl(context.request().absoluteURI() + "/" + todo.getId());
        return todo;
    }

    // PATCH /todos/:todoId
    private void handleUpdateTodo(RoutingContext context) {

        try {
            String todoID = context.request().getParam("todoId"); // (1)
            final Todo newTodo = new Todo(context.getBodyAsString()); // (2)
            // handle error
            if (todoID == null || newTodo == null) {
                sendError(400, context.response());
                return;
            }
            redisClient.hget(Constants.REDIS_TODO_KEY, todoID, x -> { // (3)
                if (x.succeeded()) {
                    String result = x.result();
                    if (result == null)
                        sendError(404, context.response()); // (4)
                    else {
                        Todo oldTodo = new Todo(result);
                        String response = Json.encodePrettily(oldTodo.merge(newTodo)); // (5)
                        redisClient.hset(Constants.REDIS_TODO_KEY, todoID, response, res -> { // (6)
                            if (res.succeeded()) {
                                context.response()
                                        .putHeader("content-type", "application/json")
                                        .end(response); // (7)
                            }
                        });
                    }
                } else
                    sendError(503, context.response());
            });
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private void handleDeleteOne(RoutingContext context) {

        String todoID = context.request().getParam("todoId");
        redisClient.hdel(Constants.REDIS_TODO_KEY, todoID, res -> {
            if (res.succeeded())
                context.response().setStatusCode(204).end();
            else
                sendError(503, context.response());
        });
    }

    private void handleDeleteAll(RoutingContext context) {

        redisClient.del(Constants.REDIS_TODO_KEY, res -> {
            if (res.succeeded())
                context.response().setStatusCode(204).end();
            else
                sendError(503, context.response());
        });
    }

    private void sendError(int statusCode, HttpServerResponse response) {

        response.setStatusCode(statusCode).end();
    }
}
