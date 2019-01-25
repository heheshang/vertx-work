/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.vertx.kue.service.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import com.vertx.kue.queue.JobState;
import io.vertx.rxjava.core.Vertx;
import com.vertx.kue.queue.Job;
import io.vertx.core.json.JsonArray;
import java.util.List;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link com.vertx.kue.service.JobService original} non RX-ified interface using Vert.x codegen.
 */

@io.vertx.lang.rxjava.RxGen(com.vertx.kue.service.JobService.class)
public class JobService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JobService that = (JobService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rxjava.TypeArg<JobService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new JobService((com.vertx.kue.service.JobService) obj),
    JobService::getDelegate
  );

  private final com.vertx.kue.service.JobService delegate;
  
  public JobService(com.vertx.kue.service.JobService delegate) {
    this.delegate = delegate;
  }

  public com.vertx.kue.service.JobService getDelegate() {
    return delegate;
  }

  /**
   * Factory method for creating a {@link com.vertx.kue.service.rxjava.JobService} instance.
   * @param vertx Vertx instance
   * @param config configuration
   * @return the new {@link com.vertx.kue.service.rxjava.JobService} instance
   */
  public static JobService create(Vertx vertx, JsonObject config) { 
    JobService ret = JobService.newInstance(com.vertx.kue.service.JobService.create(vertx.getDelegate(), config));
    return ret;
  }

  /**
   * Factory method for creating a {@link com.vertx.kue.service.rxjava.JobService} service proxy.
   * This is useful for doing RPCs.
   * @param vertx Vertx instance
   * @param address event bus address of RPC
   * @return the new {@link com.vertx.kue.service.rxjava.JobService} service proxy
   */
  public static JobService createProxy(Vertx vertx, String address) { 
    JobService ret = JobService.newInstance(com.vertx.kue.service.JobService.createProxy(vertx.getDelegate(), address));
    return ret;
  }

  /**
   * Get the certain from backend by id.
   *  获取任务的方法非常简单。直接利用hgetall命令从Redis中取出对应的任务即可
   * @param id job id
   * @param handler async result handler
   * @return 
   */
  public JobService getJob(long id, Handler<AsyncResult<Job>> handler) { 
    delegate.getJob(id, handler);
    return this;
  }

  /**
   * Get the certain from backend by id.
   *  获取任务的方法非常简单。直接利用hgetall命令从Redis中取出对应的任务即可
   * @param id job id
   * @return 
   */
  public Single<Job> rxGetJob(long id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getJob(id, fut);
    }));
  }

  /**
   * Remove a job by id.
   *  我们可以将此方法看作是getJob和Job#remove两个方法的组合
   * @param id job id
   * @param handler async result handler
   * @return 
   */
  public JobService removeJob(long id, Handler<AsyncResult<Void>> handler) { 
    delegate.removeJob(id, handler);
    return this;
  }

  /**
   * Remove a job by id.
   *  我们可以将此方法看作是getJob和Job#remove两个方法的组合
   * @param id job id
   * @return 
   */
  public Single<Void> rxRemoveJob(long id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      removeJob(id, fut);
    }));
  }

  /**
   * Judge whether a job with certain id exists.
   *  使用exists命令判断对应id的任务是否存在
   * @param id job id
   * @param handler async result handler
   * @return 
   */
  public JobService existsJob(long id, Handler<AsyncResult<Boolean>> handler) { 
    delegate.existsJob(id, handler);
    return this;
  }

  /**
   * Judge whether a job with certain id exists.
   *  使用exists命令判断对应id的任务是否存在
   * @param id job id
   * @return 
   */
  public Single<Boolean> rxExistsJob(long id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      existsJob(id, fut);
    }));
  }

  /**
   * Get job log by id.
   * 使用lrange命令从vertx_kue:job:{id}:log列表中取出日志。
   * @param id job id
   * @param handler async result handler
   * @return 
   */
  public JobService getJobLog(long id, Handler<AsyncResult<JsonArray>> handler) { 
    delegate.getJobLog(id, handler);
    return this;
  }

  /**
   * Get job log by id.
   * 使用lrange命令从vertx_kue:job:{id}:log列表中取出日志。
   * @param id job id
   * @return 
   */
  public Single<JsonArray> rxGetJobLog(long id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getJobLog(id, fut);
    }));
  }

  /**
   * Get a list of job in certain state in range (from, to) with order.
   *  指定状态，对应的key为vertx_kue:jobs:{state}。
   * @param state expected job state
   * @param from from
   * @param to to
   * @param order range order
   * @param handler async result handler
   * @return 
   */
  public JobService jobRangeByState(String state, long from, long to, String order, Handler<AsyncResult<List<Job>>> handler) { 
    delegate.jobRangeByState(state, from, to, order, handler);
    return this;
  }

  /**
   * Get a list of job in certain state in range (from, to) with order.
   *  指定状态，对应的key为vertx_kue:jobs:{state}。
   * @param state expected job state
   * @param from from
   * @param to to
   * @param order range order
   * @return 
   */
  public Single<List<Job>> rxJobRangeByState(String state, long from, long to, String order) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      jobRangeByState(state, from, to, order, fut);
    }));
  }

  /**
   * Get a list of job in certain state and type in range (from, to) with order.
   *  指定状态和类型，对应的key为vertx_kue:jobs:{type}:{state}。
   * @param type expected job type
   * @param state expected job state
   * @param from from
   * @param to to
   * @param order range order
   * @param handler async result handler
   * @return 
   */
  public JobService jobRangeByType(String type, String state, long from, long to, String order, Handler<AsyncResult<List<Job>>> handler) { 
    delegate.jobRangeByType(type, state, from, to, order, handler);
    return this;
  }

  /**
   * Get a list of job in certain state and type in range (from, to) with order.
   *  指定状态和类型，对应的key为vertx_kue:jobs:{type}:{state}。
   * @param type expected job type
   * @param state expected job state
   * @param from from
   * @param to to
   * @param order range order
   * @return 
   */
  public Single<List<Job>> rxJobRangeByType(String type, String state, long from, long to, String order) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      jobRangeByType(type, state, from, to, order, fut);
    }));
  }

  /**
   * Get a list of job in range (from, to) with order.
   * 对应的key为vertx_kue:jobs。
   * @param from from
   * @param to to
   * @param order range order
   * @param handler async result handler
   * @return 
   */
  public JobService jobRange(long from, long to, String order, Handler<AsyncResult<List<Job>>> handler) { 
    delegate.jobRange(from, to, order, handler);
    return this;
  }

  /**
   * Get a list of job in range (from, to) with order.
   * 对应的key为vertx_kue:jobs。
   * @param from from
   * @param to to
   * @param order range order
   * @return 
   */
  public Single<List<Job>> rxJobRange(long from, long to, String order) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      jobRange(from, to, order, fut);
    }));
  }

  /**
   * Get cardinality by job type and state.
   * 利用zcard命令获取某一指定状态和类型下任务的数量。
   * @param type job type
   * @param state job state
   * @param handler async result handler
   * @return 
   */
  public JobService cardByType(String type, JobState state, Handler<AsyncResult<Long>> handler) { 
    delegate.cardByType(type, state, handler);
    return this;
  }

  /**
   * Get cardinality by job type and state.
   * 利用zcard命令获取某一指定状态和类型下任务的数量。
   * @param type job type
   * @param state job state
   * @return 
   */
  public Single<Long> rxCardByType(String type, JobState state) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      cardByType(type, state, fut);
    }));
  }

  /**
   * Get cardinality by job state.
   *利用zcard命令获取某一指定状态下任务的数量。
   * @param state job state
   * @param handler async result handler
   * @return 
   */
  public JobService card(JobState state, Handler<AsyncResult<Long>> handler) { 
    delegate.card(state, handler);
    return this;
  }

  /**
   * Get cardinality by job state.
   *利用zcard命令获取某一指定状态下任务的数量。
   * @param state job state
   * @return 
   */
  public Single<Long> rxCard(JobState state) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      card(state, fut);
    }));
  }

  /**
   * Get cardinality of completed jobs.
   * @param type job type; if null, then return global metrics
   * @param handler async result handler
   * @return 
   */
  public JobService completeCount(String type, Handler<AsyncResult<Long>> handler) { 
    delegate.completeCount(type, handler);
    return this;
  }

  /**
   * Get cardinality of completed jobs.
   * @param type job type; if null, then return global metrics
   * @return 
   */
  public Single<Long> rxCompleteCount(String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      completeCount(type, fut);
    }));
  }

  /**
   * Get cardinality of failed jobs.
   * @param type job type; if null, then return global metrics
   * @param handler 
   * @return 
   */
  public JobService failedCount(String type, Handler<AsyncResult<Long>> handler) { 
    delegate.failedCount(type, handler);
    return this;
  }

  /**
   * Get cardinality of failed jobs.
   * @param type job type; if null, then return global metrics
   * @return 
   */
  public Single<Long> rxFailedCount(String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      failedCount(type, fut);
    }));
  }

  /**
   * Get cardinality of inactive jobs.
   * @param type job type; if null, then return global metrics
   * @param handler 
   * @return 
   */
  public JobService inactiveCount(String type, Handler<AsyncResult<Long>> handler) { 
    delegate.inactiveCount(type, handler);
    return this;
  }

  /**
   * Get cardinality of inactive jobs.
   * @param type job type; if null, then return global metrics
   * @return 
   */
  public Single<Long> rxInactiveCount(String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      inactiveCount(type, fut);
    }));
  }

  /**
   * Get cardinality of active jobs.
   * @param type job type; if null, then return global metrics
   * @param handler 
   * @return 
   */
  public JobService activeCount(String type, Handler<AsyncResult<Long>> handler) { 
    delegate.activeCount(type, handler);
    return this;
  }

  /**
   * Get cardinality of active jobs.
   * @param type job type; if null, then return global metrics
   * @return 
   */
  public Single<Long> rxActiveCount(String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      activeCount(type, fut);
    }));
  }

  /**
   * Get cardinality of delayed jobs.
   * @param type job type; if null, then return global metrics
   * @param handler 
   * @return 
   */
  public JobService delayedCount(String type, Handler<AsyncResult<Long>> handler) { 
    delegate.delayedCount(type, handler);
    return this;
  }

  /**
   * Get cardinality of delayed jobs.
   * @param type job type; if null, then return global metrics
   * @return 
   */
  public Single<Long> rxDelayedCount(String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      delayedCount(type, fut);
    }));
  }

  /**
   * Get the job types present.
   * 利用smembers命令获取vertx_kue:job:types集合中存储的所有的任务类型。
   * @param handler async result handler
   * @return 
   */
  public JobService getAllTypes(Handler<AsyncResult<List<String>>> handler) { 
    delegate.getAllTypes(handler);
    return this;
  }

  /**
   * Get the job types present.
   * 利用smembers命令获取vertx_kue:job:types集合中存储的所有的任务类型。
   * @return 
   */
  public Single<List<String>> rxGetAllTypes() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getAllTypes(fut);
    }));
  }

  /**
   * Return job ids with the given .
   * 使用zrange获取某一指定状态下所有任务的ID。
   * @param state job state
   * @param handler async result handler
   * @return 
   */
  public JobService getIdsByState(JobState state, Handler<AsyncResult<List<Long>>> handler) { 
    delegate.getIdsByState(state, handler);
    return this;
  }

  /**
   * Return job ids with the given .
   * 使用zrange获取某一指定状态下所有任务的ID。
   * @param state job state
   * @return 
   */
  public Single<List<Long>> rxGetIdsByState(JobState state) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getIdsByState(state, fut);
    }));
  }

  /**
   * Get queue work time in milliseconds.
   * 使用get命令从vertx_kue:stats:work-time中获取Vert.x Kue的工作时间。
   * @param handler async result handler
   * @return 
   */
  public JobService getWorkTime(Handler<AsyncResult<Long>> handler) { 
    delegate.getWorkTime(handler);
    return this;
  }

  /**
   * Get queue work time in milliseconds.
   * 使用get命令从vertx_kue:stats:work-time中获取Vert.x Kue的工作时间。
   * @return 
   */
  public Single<Long> rxGetWorkTime() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getWorkTime(fut);
    }));
  }


  public static  JobService newInstance(com.vertx.kue.service.JobService arg) {
    return arg != null ? new JobService(arg) : null;
  }
}
