require 'vertx-kue-service-module/job_service'
require 'vertx/vertx'
require 'vertx/message'
require 'vertx/util/utils.rb'
# Generated from com.vertx.kue.CallbackKue
module VertxKueRootModule
  #  A callback-based {::VertxKueRootModule::CallbackKue} interface for Vert.x Codegen to support polyglot languages.
  class CallbackKue < ::VertxKueServiceModule::JobService
    # @private
    # @param j_del [::VertxKueRootModule::CallbackKue] the java delegate
    def initialize(j_del)
      super(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxKueRootModule::CallbackKue] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == CallbackKue
    end
    def @@j_api_type.wrap(obj)
      CallbackKue.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::ComVertxKue::CallbackKue.java_class
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
    # @param [::Vertx::Vertx] vertx 
    # @param [Hash{String => Object}] config 
    # @return [::VertxKueRootModule::CallbackKue]
    def self.create_kue(vertx=nil,config=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::ComVertxKue::CallbackKue.java_method(:createKue, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)),::VertxKueRootModule::CallbackKue)
      end
      raise ArgumentError, "Invalid arguments when calling create_kue(#{vertx},#{config})"
    end
    # @param [String] type 
    # @param [Hash{String => Object}] data 
    # @return [Hash]
    def create_job(type=nil,data=nil)
      if type.class == String && data.class == Hash && !block_given?
        return @j_del.java_method(:createJob, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(type,::Vertx::Util::Utils.to_json_object(data)) != nil ? JSON.parse(@j_del.java_method(:createJob, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(type,::Vertx::Util::Utils.to_json_object(data)).toJson.encode) : nil
      end
      raise ArgumentError, "Invalid arguments when calling create_job(#{type},#{data})"
    end
    # @param [String] eventType 
    # @yield 
    # @return [self]
    def on(eventType=nil)
      if eventType.class == String && block_given?
        @j_del.java_method(:on, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(eventType,(Proc.new { |event| yield(::Vertx::Util::Utils.safe_create(event,::Vertx::Message, nil)) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling on(#{eventType})"
    end
    # @param [Hash] job 
    # @yield 
    # @return [self]
    def save_job(job=nil)
      if job.class == Hash && block_given?
        @j_del.java_method(:saveJob, [Java::ComVertxKueQueue::Job.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::ComVertxKueQueue::Job.new(::Vertx::Util::Utils.to_json_object(job)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save_job(#{job})"
    end
    # @param [Hash] job 
    # @param [Fixnum] complete 
    # @param [Fixnum] total 
    # @yield 
    # @return [self]
    def job_progress(job=nil,complete=nil,total=nil)
      if job.class == Hash && complete.class == Fixnum && total.class == Fixnum && block_given?
        @j_del.java_method(:jobProgress, [Java::ComVertxKueQueue::Job.java_class,Java::int.java_class,Java::int.java_class,Java::IoVertxCore::Handler.java_class]).call(Java::ComVertxKueQueue::Job.new(::Vertx::Util::Utils.to_json_object(job)),complete,total,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_progress(#{job},#{complete},#{total})"
    end
    # @param [Hash] job 
    # @return [self]
    def job_done(job=nil)
      if job.class == Hash && !block_given?
        @j_del.java_method(:jobDone, [Java::ComVertxKueQueue::Job.java_class]).call(Java::ComVertxKueQueue::Job.new(::Vertx::Util::Utils.to_json_object(job)))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_done(#{job})"
    end
    # @param [Hash] job 
    # @param [Exception] ex 
    # @return [self]
    def job_done_fail(job=nil,ex=nil)
      if job.class == Hash && ex.is_a?(Exception) && !block_given?
        @j_del.java_method(:jobDoneFail, [Java::ComVertxKueQueue::Job.java_class,Java::JavaLang::Throwable.java_class]).call(Java::ComVertxKueQueue::Job.new(::Vertx::Util::Utils.to_json_object(job)),::Vertx::Util::Utils.to_throwable(ex))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling job_done_fail(#{job},#{ex})"
    end
    # @param [String] type 
    # @param [Fixnum] n 
    # @yield 
    # @return [self]
    def process(type=nil,n=nil)
      if type.class == String && n.class == Fixnum && block_given?
        @j_del.java_method(:process, [Java::java.lang.String.java_class,Java::int.java_class,Java::IoVertxCore::Handler.java_class]).call(type,n,(Proc.new { |event| yield(event != nil ? JSON.parse(event.toJson.encode) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling process(#{type},#{n})"
    end
    # @param [String] type 
    # @param [Fixnum] n 
    # @yield 
    # @return [self]
    def process_blocking(type=nil,n=nil)
      if type.class == String && n.class == Fixnum && block_given?
        @j_del.java_method(:processBlocking, [Java::java.lang.String.java_class,Java::int.java_class,Java::IoVertxCore::Handler.java_class]).call(type,n,(Proc.new { |event| yield(event != nil ? JSON.parse(event.toJson.encode) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling process_blocking(#{type},#{n})"
    end
  end
end
