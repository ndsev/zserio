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
  class StubInterface {
   public:
    virtual ~StubInterface() {}
    virtual ::grpc::Status ${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ${responseTypeFullName}* response) = 0;
    std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>> Async${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>>(Async${name}Raw(context, request, cq));
    }
    std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>> PrepareAsync${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>>(PrepareAsync${name}Raw(context, request, cq));
    }
  private:
    virtual ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>* Async${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
    virtual ::grpc::ClientAsyncResponseReaderInterface< ${responseTypeFullName}>* PrepareAsync${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
  };
  class Stub final : public StubInterface {
   public:
    Stub(const std::shared_ptr< ::grpc::ChannelInterface>& channel);
    ::grpc::Status ${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ${responseTypeFullName}* response) override;
    std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>> Async${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>>(Async${name}Raw(context, request, cq));
    }
    std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>> PrepareAsync${name}(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) {
      return std::unique_ptr< ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>>(PrepareAsync${name}Raw(context, request, cq));
    }

   private:
    std::shared_ptr< ::grpc::ChannelInterface> channel_;
    ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>* Async${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    ::grpc::ClientAsyncResponseReader< ${responseTypeFullName}>* PrepareAsync${name}Raw(::grpc::ClientContext* context, const ${requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
    const ::grpc::internal::RpcMethod rpcmethod_${name}_;
  };
  static std::unique_ptr<Stub> NewStub(const std::shared_ptr< ::grpc::ChannelInterface>& channel, const ::grpc::StubOptions& options = ::grpc::StubOptions());

  class Service : public ::grpc::Service {
   public:
    Service();
    virtual ~Service();
    virtual ::grpc::Status ${name}(::grpc::ServerContext* context, const ${requestTypeFullName}* request, ${responseTypeFullName}* response);
  };
  template <class BaseClass>
  class WithAsyncMethod_${name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithAsyncMethod_${name}() {
      ::grpc::Service::MarkMethodAsync(0);
    }
    ~WithAsyncMethod_${name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable synchronous version of this method
    ::grpc::Status ${name}(::grpc::ServerContext* context, const ${requestTypeFullName}* request, ${responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
    void Request${name}(::grpc::ServerContext* context, ${requestTypeFullName}* request, ::grpc::ServerAsyncResponseWriter< ${responseTypeFullName}>* response, ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag) {
      ::grpc::Service::RequestAsyncUnary(0, context, request, response, new_call_cq, notification_cq, tag);
    }
  };
  typedef WithAsyncMethod_${name}<Service > AsyncService;
  template <class BaseClass>
  class WithGenericMethod_${name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithGenericMethod_${name}() {
      ::grpc::Service::MarkMethodGeneric(0);
    }
    ~WithGenericMethod_${name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable synchronous version of this method
    ::grpc::Status ${name}(::grpc::ServerContext* context, const ${requestTypeFullName}* request, ${responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
  };
  template <class BaseClass>
  class WithStreamedUnaryMethod_${name} : public BaseClass {
   private:
    void BaseClassMustBeDerivedFromService(const Service *service) {}
   public:
    WithStreamedUnaryMethod_${name}() {
      ::grpc::Service::MarkMethodStreamed(0,
        new ::grpc::internal::StreamedUnaryHandler< ${requestTypeFullName}, ${responseTypeFullName}>(std::bind(&WithStreamedUnaryMethod_${name}<BaseClass>::Streamed${name}, this, std::placeholders::_1, std::placeholders::_2)));
    }
    ~WithStreamedUnaryMethod_${name}() override {
      BaseClassMustBeDerivedFromService(this);
    }
    // disable regular version of this method
    ::grpc::Status ${name}(::grpc::ServerContext* context, const ${requestTypeFullName}* request, ${responseTypeFullName}* response) final override {
      abort();
      return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
    }
    // replace default version of method with streamed unary
    virtual ::grpc::Status Streamed${name}(::grpc::ServerContext* context, ::grpc::ServerUnaryStreamer< ${requestTypeFullName}, ${responseTypeFullName}>* server_unary_streamer) = 0;
  };
  typedef WithStreamedUnaryMethod_${name}<Service > StreamedUnaryService;
  typedef Service SplitStreamedService;
  typedef WithStreamedUnaryMethod_${name}<Service > StreamedService;
};

<@namespace_end package.path/>

namespace grpc {

template <class T>
class SerializationTraits<T, typename std::enable_if<std::is_base_of<
                                 ${requestTypeFullName}, T>::value>::type> {
 public:
  static Status Serialize(const ${requestTypeFullName}& msg,
                          ByteBuffer* bb,
                          bool* own_buffer);
  static Status Deserialize(ByteBuffer* buffer, ${requestTypeFullName}* msg);
};

template <class T>
class SerializationTraits<T, typename std::enable_if<std::is_base_of<
                                 ${responseTypeFullName}, T>::value>::type> {
 public:
  static Status Serialize(const ${responseTypeFullName}& msg,
                          ByteBuffer* bb,
                          bool* own_buffer);
  static Status Deserialize(ByteBuffer* buffer, ${responseTypeFullName}* msg);
};

}  // namespace grpc


<@include_guard_end package.path, name/>
