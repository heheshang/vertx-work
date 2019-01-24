package com.vertx.demo.verticle;

import com.vertx.demo.Todo;
import com.vertx.demo.constants.Constants;
import com.vertx.demo.service.TodoService;
import com.vertx.demo.service.impl.JdbcTodoService;
import com.vertx.demo.service.impl.RedisTodoService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-24-下午 12:44
 */
@Slf4j
public class TodoVerticle extends AbstractVerticle {

    private static final String HOST = "0.0.0.0";

    private static final int port = 8082;

    private TodoService todoService;

    private void initData() {
        // TODO

        final String serviceType = config().getString("service.type", "redis");

        log.info("Service Type :" + serviceType);

        switch (serviceType) {
            case "jdbc":
                todoService = new JdbcTodoService(vertx, config());
                break;
            case "redis":
            default:
                RedisOptions config = new RedisOptions()
                        .setHost(config().getString("redis.host", "127.0.0.1"))
                        .setPort(config().getInteger("redis.port", 6379));
                todoService = new RedisTodoService(vertx, config);
        }

   /*     todoService.initData().setHandler(res -> {
            if (res.failed()) {
                log.error("Persistence service is not running!\"");
                res.cause().printStackTrace();
            }
        });*/

    }

    @Override
    public void start(Future<Void> future) throws Exception {

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
                .listen(port, HOST, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });

        initData();
    }

    private void handleDeleteAll(RoutingContext context) {

        this.todoService.deleteAll().setHandler(this.deleteResultHandler(context));
    }

    private void handleDeleteOne(RoutingContext context) {

        String todoID = context.request().getParam("todoId");
        this.todoService.delete(todoID).setHandler(this.deleteResultHandler(context));
    }

    private void handleUpdateTodo(RoutingContext context) {

        try {
            String todoID = context.request().getParam("todoId");
            final Todo newTodo = new Todo(context.getBodyAsString());
            // handle error
            if (todoID == null) {
                sendError(400, context.response());
                return;
            }
            this.todoService.update(todoID, newTodo)
                    .setHandler(this.resultHandler(context, res -> {
                        if (res == null)
                            notFound(context);
                        else {
                            final String encoded = Json.encodePrettily(res);
                            context.response()
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        }
                    }));
        } catch (DecodeException e) {
            badRequest(context);
        }

    }

    private void handleCreateTodo(RoutingContext context) {

        try {
            final Todo todo = wrapObject(new Todo(context.getBodyAsString()), context);
            final String encoded = Json.encodePrettily(todo);

            this.todoService.insert(todo).setHandler(this.resultHandler(context, res -> {
                if (res) {
                    context.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json")
                            .end(encoded);
                } else {
                    this.serviceUnavailable(context);
                }
            }));


        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private void handleGetAll(RoutingContext context) {

        this.todoService.getAll().setHandler(this.resultHandler(context, res -> {
            if (res == null) {
                serviceUnavailable(context);
            } else {
                final String encoded = Json.encodePrettily(res);
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));

    }

    private void handleGetTodo(RoutingContext context) {
        // (1)
        String todoID = context.request().getParam("todoId");
        if (todoID == null) {
            // (2)
            sendError(400, context.response());
        } else {
            // (3)
            this.todoService.getCertain(todoID)
                    .setHandler(this.resultHandler(context, res -> {
                        if (!res.isPresent()) {
                            notFound(context);
                        } else {
                            final String encoded = Json.encodePrettily(res.get());
                            context.response()
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        }
                    }));

        }
    }


    private void sendError(int statusCode, HttpServerResponse response) {

        response.setStatusCode(statusCode).end();
    }

    private void badRequest(RoutingContext context) {

        context.response().setStatusCode(400).end();
    }

    private void notFound(RoutingContext context) {

        context.response().setStatusCode(404).end();
    }

    private void serviceUnavailable(RoutingContext context) {

        context.response().setStatusCode(503).end();
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

    /**
     * Wrap the result handler with failure handler (503 Service Unavailable)
     */
    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {

        return res -> {
            if (res.succeeded()) {
                consumer.accept(res.result());
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private Handler<AsyncResult<Boolean>> deleteResultHandler(RoutingContext context) {

        return res -> {
            if (res.succeeded()) {
                if (res.result()) {
                    context.response().setStatusCode(204).end();
                } else {
                    serviceUnavailable(context);
                }
            } else {
                serviceUnavailable(context);
            }
        };

    }
}
