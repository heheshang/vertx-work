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
        // 给路由器绑定了一个全局的BodyHandler （3），它的作用是处理HTTP请求正文并获取其中的数据。
        // 比如，在实现添加待办事项逻辑的时候，我们需要读取请求正文中的JSON数据，这时候我们就可以用BodyHandler。
        router.route().handler(BodyHandler.create());
        // Todo :routes
        //添加待办事项: POST /todos
        //获取某一待办事项: GET /todos/:todoId
        //获取所有待办事项: GET /todos
        //更新待办事项: PATCH /todos/:todoId
        //删除某一待办事项: DELETE /todos/:todoId
        //删除所有待办事项: DELETE /todos
        /**
         * 代码很直观、明了。我们用对应的方法（如get,post,patch等等）将路由路径与路由器绑定，
         * 并且我们调用handler方法给每个路由绑定上对应的Handler，接受的Handler类型为Handler<RoutingContext>。
         * 这里我们分别绑定了六个方法引用，它们的形式都类似于这样：
         */
        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.post(Constants.API_CREATE).handler(this::handleCreateTodo);
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);

        //最后，我们通过vertx.createHttpServer()方法来创建一个HTTP服务端 （4）。
        // 注意这个功能是Vert.x Core提供的底层功能之一。然后我们将我们的路由处理器绑定到服务端上，
        // 这也是Vert.x Web的核心。
        // 你可能不熟悉router::accept这样的表示，这是Java 8中的 方法引用，它相当于一个分发路由的Handler。
        // 当有请求到达时，Vert.x会调用accept方法。然后我们通过listen方法监听8082端口。
        // 因为创建服务端的过程可能失败，因此我们还需要给listen方法传递一个Handler来检查服务端是否创建成功。
        // 正如我们前面所提到的，我们可以使用future.complete来表示过程成功，或者用future.fail来表示过程失败
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
        log.info("请求信息为【{}】", todoID);
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
                            log.info("返回信息为：【{}】", res.get());
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
                log.info("返回信息为：【{}】", Json.encode(res.result()));
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
