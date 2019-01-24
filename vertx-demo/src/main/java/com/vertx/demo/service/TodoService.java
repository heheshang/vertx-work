package com.vertx.demo.service;

import com.vertx.demo.Todo;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2019-01-24-上午 11:34
 */
public interface TodoService {

    Future<Boolean> initData();

    Future<Boolean> insert(Todo todo);

    Future<List<Todo>> getAll();

    Future<Optional<Todo>> getCertain(String todoID);

    Future<Todo> update(String todoId,Todo newTodo);

    Future<Boolean> delete(String todoId);

    Future<Boolean> deleteAll();
}
