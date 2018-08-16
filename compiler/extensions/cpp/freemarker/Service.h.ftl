<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@user_includes headerUserIncludes, true/>
#include <grpcpp/impl/codegen/async_generic_service.h>
#include <grpcpp/impl/codegen/async_stream.h>
#include <grpcpp/impl/codegen/async_unary_call.h>
#include <grpcpp/impl/codegen/method_handler_impl.h>
#include <grpcpp/impl/codegen/proto_utils.h>
#include <grpcpp/impl/codegen/rpc_method.h>
#include <grpcpp/impl/codegen/service_type.h>
#include <grpcpp/impl/codegen/status.h>
#include <grpcpp/impl/codegen/stub_options.h>
#include <grpcpp/impl/codegen/sync_stream.h>

namespace grpc
{

class CompletionQueue;
class Channel;
class ServerCompletionQueue;
class ServerContext;

} // namespace grpc

<@namespace_begin package.path/>

class ${name} final
{
public:
    static constexpr char const* service_full_name()
    {
        return "<#if package.name?has_content>${package.name}.</#if>${name}";
    }

    class StubInterface
    {
    public:
        virtual ~StubInterface() {}

<#list rpcList as rpc>
        virtual ::grpc::Status ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) = 0;

        typedef ::grpc::ClientAsyncResponseReaderInterface<${rpc.responseTypeFullName}>
                IClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<IClientAsync${rpc.name}Reader> IClientAsync${rpc.name}ReaderPtr;

        IClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq));
        }

        IClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return IClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }

</#list>
    private:
<#list rpcList as rpc>
        virtual IClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) = 0;
        virtual IClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(
                ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request,
                ::grpc::CompletionQueue* cq) = 0;
    <#if rpc?has_next>

    </#if>
</#list>
    };

    class Stub final : public StubInterface
    {
    public:
        Stub(const std::shared_ptr<::grpc::ChannelInterface>& channel);

<#list rpcList as rpc>
        ::grpc::Status ${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response) override;

        typedef ::grpc::ClientAsyncResponseReader<${rpc.responseTypeFullName}> ClientAsync${rpc.name}Reader;
        typedef std::unique_ptr<ClientAsync${rpc.name}Reader> ClientAsync${rpc.name}ReaderPtr;

        ClientAsync${rpc.name}ReaderPtr Async${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderPtr(Async${rpc.name}Raw(context, request, cq));
        }

        ClientAsync${rpc.name}ReaderPtr PrepareAsync${rpc.name}(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
        {
            return ClientAsync${rpc.name}ReaderPtr(PrepareAsync${rpc.name}Raw(context, request, cq));
        }

</#list>
    private:
        std::shared_ptr<::grpc::ChannelInterface> channel_;
<#list rpcList as rpc>

        ClientAsync${rpc.name}Reader* Async${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
        ClientAsync${rpc.name}Reader* PrepareAsync${rpc.name}Raw(::grpc::ClientContext* context,
                const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq) override;
        const ::grpc::internal::RpcMethod rpcmethod_${rpc.name};
</#list>
    };
    static std::unique_ptr<Stub> NewStub(const std::shared_ptr<::grpc::ChannelInterface>& channel,
            const ::grpc::StubOptions& options = ::grpc::StubOptions());

    class Service : public ::grpc::Service
    {
    public:
        Service();
        virtual ~Service();
<#list rpcList as rpc>
        virtual ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response);
</#list>
    };

<#list rpcList as rpc>
    template <class BaseClass>
    class WithAsyncMethod_${rpc.name} : public BaseClass
    {
    public:
        WithAsyncMethod_${rpc.name}()
        {
            ::grpc::Service::MarkMethodAsync(${rpc?index});
        }

        ~WithAsyncMethod_${rpc.name}() override
        {
            BaseClassMustBeDerivedFromService(this);
        }

        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ${rpc.requestTypeFullName}* request,
                ::grpc::ServerAsyncResponseWriter<${rpc.responseTypeFullName}>* response,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncUnary(
                    ${rpc?index}, context, request, response, new_call_cq, notification_cq, tag);
        }

    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

    template <class BaseClass>
    class WithGenericMethod_${rpc.name} : public BaseClass
    {
    public:
        WithGenericMethod_${rpc.name}()
        {
            ::grpc::Service::MarkMethodGeneric(${rpc?index});
        }

        ~WithGenericMethod_${rpc.name}() override
        {
            BaseClassMustBeDerivedFromService(this);
        }

        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }
    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

    template <class BaseClass>
    class WithRawMethod_${rpc.name} : public BaseClass
    {
    public:
        WithRawMethod_${rpc.name}()
        {
            ::grpc::Service::MarkMethodRaw(${rpc?index});
        }

        ~WithRawMethod_${rpc.name}() override
        {
            BaseClassMustBeDerivedFromService(this);
        }

        // disable synchronous version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        void Request${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ByteBuffer* request, ::grpc::ServerAsyncResponseWriter<::grpc::ByteBuffer>* response,
                ::grpc::CompletionQueue* new_call_cq, ::grpc::ServerCompletionQueue* notification_cq, void *tag)
        {
            ::grpc::Service::RequestAsyncUnary(
                    ${rpc?index}, context, request, response, new_call_cq, notification_cq, tag);
        }

    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

    template <class BaseClass>
    class WithStreamedUnaryMethod_${rpc.name} : public BaseClass
    {
    public:
        WithStreamedUnaryMethod_${rpc.name}()
        {
            typedef ::grpc::internal::StreamedUnaryHandler<
                    ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> StreamUnaryHandler;

            ::grpc::Service::MarkMethodStreamed(${rpc?index},
                    new StreamUnaryHandler(std::bind(
                            &WithStreamedUnaryMethod_${rpc.name}<BaseClass>::Streamed${rpc.name},
                            this, std::placeholders::_1, std::placeholders::_2)));
        }

        ~WithStreamedUnaryMethod_${rpc.name}() override
        {
            BaseClassMustBeDerivedFromService(this);
        }

        // disable regular version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context,
                const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response) final override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        // replace default version of method with streamed unary
        virtual ::grpc::Status Streamed${rpc.name}(::grpc::ServerContext* context,
                ::grpc::ServerUnaryStreamer<${rpc.requestTypeFullName},
                ${rpc.responseTypeFullName}>* server_unary_streamer) = 0;

    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

</#list>
<#macro typedef_with_method methodName rpcList serviceName>
    <#list rpcList as rpc>
        <#if rpc?is_first>
    typedef <#rt>
            <#if rpc?has_next>
                <#lt>With${methodName}Method_${rpc.name}<
            <#else>
                <#lt>With${methodName}Method_${rpc.name}<<#rt>
            </#if>
        <#elseif rpc?is_last>
            With${methodName}Method_${rpc.name}<<#rt>
        <#else>
            With${methodName}Method_${rpc.name}<
        </#if>
    </#list>
        Service<#t>
    <#list rpcList as rpc>
        <#lt><#if rpc?index != 0> </#if>><#rt>
    </#list>
        <#lt> ${serviceName}Service;
</#macro>
    <@typedef_with_method "Async" rpcList "Async"/>
    <@typedef_with_method "StreamedUnary" rpcList "StreamedUnary"/>
    <@typedef_with_method "StreamedUnary" rpcList "Streamed"/>
    typedef Service SplitStreamedService;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
