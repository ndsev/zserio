<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@user_includes headerUserIncludes, true/>

#include <grpcpp/impl/codegen/async_stream.h>
#include <grpcpp/impl/codegen/async_unary_call.h>
#include <grpcpp/impl/codegen/method_handler_impl.h>
#include <grpcpp/impl/codegen/proto_utils.h>
#include <grpcpp/impl/codegen/rpc_method.h>
#include <grpcpp/impl/codegen/service_type.h>
#include <grpcpp/impl/codegen/status.h>
#include <grpcpp/impl/codegen/stub_options.h>
#include <grpcpp/impl/codegen/sync_stream.h>

namespace grpc {
class CompletionQueue;
class Channel;
class ServerCompletionQueue;
class ServerContext;
}  // namespace grpc

<@namespace_begin package.path/>

class ${name} final {
 public:
  static constexpr char const* service_full_name() {
    return "${package.name}.${name}";
  }

<#list rpcList as rpc>
// ${rpc.name}(${rpc.requestTypeFullName},${rpc.responseTypeFullName})
</#list>

  class StubInterface {
   public:
    virtual ~StubInterface() {}
<#list rpcList as rpc>
    virtual ::grpc::Status ${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) = 0;
    std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>> Async${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>>(Async${rpc.name}Raw(context, request, cq));
    }
    std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>> PrepareAsync${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>>(PrepareAsync${rpc.name}Raw(context, request, cq));
    }
  private:
    virtual ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>* Async${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
    virtual ::grpc::ClientAsyncResponseReaderInterface< ${rpc.responseTypeFullName}>* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
</#list>
  };

  class Stub final : public StubInterface {
   public:
    Stub(const std::shared_ptr< ::grpc::ChannelInterface>& channel);
<#list rpcList as rpc>
    ::grpc::Status ${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) override;
    std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>> Async${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>>(Async${rpc.name}Raw(context, request, cq));
    }
    std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>> PrepareAsync${rpc.name}(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>>(PrepareAsync${rpc.name}Raw(context, request, cq));
    }
</#list>

   private:
    std::shared_ptr< ::grpc::ChannelInterface> channel_;
<#list rpcList as rpc>
    ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>* Async${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    ::grpc::ClientAsyncResponseReader< ${rpc.responseTypeFullName}>* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    const ::grpc::internal::RpcMethod rpcmethod_${name}_${rpc.name};
</#list>
  };
  static std::unique_ptr<Stub> NewStub(const std::shared_ptr< ::grpc::ChannelInterface>& channel, const ::grpc::StubOptions& options = ::grpc::StubOptions());

  class Service : public ::grpc::Service {
   public:
    Service();
    virtual ~Service();
<#list rpcList as rpc>
    virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response);
</#list>
  };

<#list rpcList as rpc>
  template <class BaseClass>
  class WithAsyncMethod_${rpc.name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithAsyncMethod_${rpc.name}() {
      ::grpc::Service::MarkMethodAsync(0);
    }
    ~WithAsyncMethod_${rpc.name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable synchronous version of this method
    ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
    void Request${rpc.name}(::grpc::ServerContext* context, ${rpc.requestTypeFullName}* request, ::grpc::ServerAsyncResponseWriter< ${rpc.responseTypeFullName}>* response, ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag) {
      ::grpc::Service::RequestAsyncUnary(0, context, request, response, new_call_cq, notification_cq, tag);
    }
  };

  template <class BaseClass>
  class WithGenericMethod_${rpc.name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithGenericMethod_${rpc.name}() {
      ::grpc::Service::MarkMethodGeneric(0);
    }
    ~WithGenericMethod_${rpc.name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable synchronous version of this method
    ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
  };

  template <class BaseClass>
  class WithStreamedUnaryMethod_${rpc.name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithStreamedUnaryMethod_${rpc.name}() {
      ::grpc::Service::MarkMethodStreamed(0,
        new ::grpc::internal::StreamedUnaryHandler< ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>(std::bind(&WithStreamedUnaryMethod_${rpc.name}<BaseClass>::Streamed${rpc.name}, this, std::placeholders::_1, std::placeholders::_2)));
    }
    ~WithStreamedUnaryMethod_${rpc.name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable regular version of this method
    ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
    // replace default version of method with streamed unary
    virtual ::grpc::Status Streamed${rpc.name}(::grpc::ServerContext* context, ::grpc::ServerUnaryStreamer< ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>* server_unary_streamer) = 0;
  };

</#list>

  typedef <#list rpcList as rpc>WithAsyncMethod_${rpc.name}< </#list>Service<#list rpcList as rpc> ></#list> AsyncService;
  typedef <#list rpcList as rpc>WithStreamedUnaryMethod_${rpc.name}< </#list>Service<#list rpcList as rpc> ></#list> StreamedUnaryService;
  typedef Service SplitStreamedService;
  typedef <#list rpcList as rpc>WithStreamedUnaryMethod_${rpc.name}< </#list>Service<#list rpcList as rpc> ></#list> StreamedService;

};

<@namespace_end package.path/>

namespace grpc {

<#list parameterTypeNames as paramName>

template <class T>
class SerializationTraits<T, typename std::enable_if<std::is_base_of<
                                 ${paramName}, T>::value>::type> {
 public:
  static Status Serialize(const ${paramName}& msg,
                          ByteBuffer* bb,
                          bool* own_buffer);
  static Status Deserialize(ByteBuffer* buffer, ${paramName}* msg);
};
</#list>

}  // namespace grpc

<@include_guard_end package.path, name/>
