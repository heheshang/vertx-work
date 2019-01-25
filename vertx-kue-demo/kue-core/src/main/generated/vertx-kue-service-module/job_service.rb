require 'vertx/vertx'
require 'vertx/util/utils.rb'
# Generated from com.vertx.kue.service.JobService
module VertxKueServiceModule
  class JobService
    # @private
    # @param j_del [::VertxKueServiceModule::JobService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxKueServiceModule::JobService] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == JobService
    end
    def @@j_api_type.wrap(obj)
      JobService.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::ComVertxKueService::JobService.java_class
    end
    #  Factory method for creating a {::VertxKueServiceModule::JobService} instance.
    # @param [::Vertx::Vertx] vertx Vertx instance
    # @param [Hash{String => Object}] config configuration
    # @return [::VertxKueServiceModule::JobService] the new {::VertxKueServiceModule::JobService} instance
    def self.create(vertx=nil,config=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::ComVertxKueService::JobService.java_method(:create, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)),::VertxKueServiceModule::JobService)
      end
      raise ArgumentError, "Invalid arguments when calling create(#{vertx},#{config})"
    end
    #  Factory method for creating a {::VertxKueServiceModule::JobService} service proxy.
    #  This is useful for doing RPCs.
    # @param [::Vertx::Vertx] vertx Vertx instance
    # @param [String] address event bus address of RPC
    # @return [::VertxKueServiceModule::JobService] the new {::VertxKueServiceModule::JobService} service proxy
    def self.create_proxy(vertx=nil,address=nil)
      if vertx.class.method_defined?(:j_del) && address.class == String && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::ComVertxKueService::JobService.java_method(:createProxy, [Java::IoVertxCore::Vertx.java_class,Java::java.lang.String.java_class]).call(vertx.j_del,address),::VertxKueServiceModule::JobService)
      end
      raise ArgumentError, "Invalid arguments when calling create_proxy(#{vertx},#{address})"
    end
    #  Get the certain from backend by id.
    #   获取任务的方法非常简单。直接利用hgetall命令从Redis中取出对应的任务即可
    # @param [Fixnum] id job id
    # @yield async result handler
    # @return [self]
    def get_job(id=nil)
      if id.class == Fixnum && block_given?
        @j_del.java_method(:getJob, [Java::long.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_job(#{id})"
    end
    #  Remove a job by id.
    #   我们可以将此方法看作是getJob和Job#remove两个方法的组合
    # @param [Fixnum] id job id
    # @yield async result handler
    # @return [self]
    def remove_job(id=nil)
      if id.class == Fixnum && block_given?
        @j_del.java_method(:removeJob, [Java::long.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_job(#{id})"
    end
    #  Judge whether a job with certain id exists.
    #   使用exists命令判断对应id的任务是否存在
    # @param [Fixnum] id job id
    # @yield async result handler
    # @return [self]
    def exists_job(id=nil)
      if id.class == Fixnum && block_given?
        @j_del.java_method(:existsJob, [Java::long.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling exists_job(#{id})"
    end
    #  Get job log by id.
    #  使用lrange命令从vertx_kue:job:{id}:log列表中取出日志。
    # @param [Fixnum] id job id
    # @yield async result handler
    # @return [self]
    def get_job_log(id=nil)
      if id.class == Fixnum && block_given?
        @j_del.java_method(:getJobLog, [Java::long.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_job_log(#{id})"
    end
    #  Get a list of job in certain state in range (from, to) with order.
    #   指定状态，对应的key为vertx_kue:jobs:{state}。
    # @param [String] state expected job state
    # @param [Fixnum] from from
    # @param [Fixnum] to to
    # @param [String] order range order
    # @yield async result handler
    # @return [self]
    def job_range_by_state(state=nil,from=nil,to=nil,order=nil)
      if state.class == String && from.class == Fixnum && to.class == Fixnum && order.class == String && block_given?
        @j_del.java_method(:jobRangeByState, [Java::java.lang.String.java_class,Java::long.java_class,Java::long.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(state,from,to,order,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_range_by_state(#{state},#{from},#{to},#{order})"
    end
    #  Get a list of job in certain state and type in range (from, to) with order.
    #   指定状态和类型，对应的key为vertx_kue:jobs:{type}:{state}。
    # @param [String] type expected job type
    # @param [String] state expected job state
    # @param [Fixnum] from from
    # @param [Fixnum] to to
    # @param [String] order range order
    # @yield async result handler
    # @return [self]
    def job_range_by_type(type=nil,state=nil,from=nil,to=nil,order=nil)
      if type.class == String && state.class == String && from.class == Fixnum && to.class == Fixnum && order.class == String && block_given?
        @j_del.java_method(:jobRangeByType, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::long.java_class,Java::long.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,state,from,to,order,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_range_by_type(#{type},#{state},#{from},#{to},#{order})"
    end
    #  Get a list of job in range (from, to) with order.
    #  对应的key为vertx_kue:jobs。
    # @param [Fixnum] from from
    # @param [Fixnum] to to
    # @param [String] order range order
    # @yield async result handler
    # @return [self]
    def job_range(from=nil,to=nil,order=nil)
      if from.class == Fixnum && to.class == Fixnum && order.class == String && block_given?
        @j_del.java_method(:jobRange, [Java::long.java_class,Java::long.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(from,to,order,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.toJson.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_range(#{from},#{to},#{order})"
    end
    #  Get cardinality by job type and state.
    #  利用zcard命令获取某一指定状态和类型下任务的数量。
    # @param [String] type job type
    # @param [:INACTIVE,:ACTIVE,:COMPLETE,:FAILED,:DELAYED] state job state
    # @yield async result handler
    # @return [self]
    def card_by_type(type=nil,state=nil)
      if type.class == String && state.class == Symbol && block_given?
        @j_del.java_method(:cardByType, [Java::java.lang.String.java_class,Java::ComVertxKueQueue::JobState.java_class,Java::IoVertxCore::Handler.java_class]).call(type,Java::ComVertxKueQueue::JobState.valueOf(state.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling card_by_type(#{type},#{state})"
    end
    #  Get cardinality by job state.
    # 利用zcard命令获取某一指定状态下任务的数量。
    # @param [:INACTIVE,:ACTIVE,:COMPLETE,:FAILED,:DELAYED] state job state
    # @yield async result handler
    # @return [self]
    def card(state=nil)
      if state.class == Symbol && block_given?
        @j_del.java_method(:card, [Java::ComVertxKueQueue::JobState.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::ComVertxKueQueue::JobState.valueOf(state.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling card(#{state})"
    end
    #  Get cardinality of completed jobs.
    # @param [String] type job type; if null, then return global metrics
    # @yield async result handler
    # @return [self]
    def complete_count(type=nil)
      if type.class == String && block_given?
        @j_del.java_method(:completeCount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling complete_count(#{type})"
    end
    #  Get cardinality of failed jobs.
    # @param [String] type job type; if null, then return global metrics
    # @yield 
    # @return [self]
    def failed_count(type=nil)
      if type.class == String && block_given?
        @j_del.java_method(:failedCount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling failed_count(#{type})"
    end
    #  Get cardinality of inactive jobs.
    # @param [String] type job type; if null, then return global metrics
    # @yield 
    # @return [self]
    def inactive_count(type=nil)
      if type.class == String && block_given?
        @j_del.java_method(:inactiveCount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling inactive_count(#{type})"
    end
    #  Get cardinality of active jobs.
    # @param [String] type job type; if null, then return global metrics
    # @yield 
    # @return [self]
    def active_count(type=nil)
      if type.class == String && block_given?
        @j_del.java_method(:activeCount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling active_count(#{type})"
    end
    #  Get cardinality of delayed jobs.
    # @param [String] type job type; if null, then return global metrics
    # @yield 
    # @return [self]
    def delayed_count(type=nil)
      if type.class == String && block_given?
        @j_del.java_method(:delayedCount, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(type,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delayed_count(#{type})"
    end
    #  Get the job types present.
    #  利用smembers命令获取vertx_kue:job:types集合中存储的所有的任务类型。
    # @yield async result handler
    # @return [self]
    def get_all_types
      if block_given?
        @j_del.java_method(:getAllTypes, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_all_types()"
    end
    #  Return job ids with the given .
    #  使用zrange获取某一指定状态下所有任务的ID。
    # @param [:INACTIVE,:ACTIVE,:COMPLETE,:FAILED,:DELAYED] state job state
    # @yield async result handler
    # @return [self]
    def get_ids_by_state(state=nil)
      if state.class == Symbol && block_given?
        @j_del.java_method(:getIdsByState, [Java::ComVertxKueQueue::JobState.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::ComVertxKueQueue::JobState.valueOf(state.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_ids_by_state(#{state})"
    end
    #  Get queue work time in milliseconds.
    #  使用get命令从vertx_kue:stats:work-time中获取Vert.x Kue的工作时间。
    # @yield async result handler
    # @return [self]
    def get_work_time
      if block_given?
        @j_del.java_method(:getWorkTime, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_work_time()"
    end
  end
end
