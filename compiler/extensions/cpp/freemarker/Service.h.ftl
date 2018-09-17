<#include "FileHeader.inc.ftl">
<#include "Service.h.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include "<@include_path rootPackage.path, "GrpcSerializationTraits.h"/>"

<@user_includes headerUserIncludes, true/>
#include <grpcpp/impl/codegen/async_generic_service.h>
#include <grpcpp/impl/codegen/async_stream.h>
#include <grpcpp/impl/codegen/async_unary_call.h>
#include <grpcpp/impl/codegen/method_handler_impl.h>
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
        <@stub_interface_header_public rpc/>

</#list>
    private:
<#list rpcList as rpc>
        <@stub_interface_header_private rpc/>
    <#if rpc?has_next>

    </#if>
</#list>
    };

    class Stub final : public StubInterface
    {
    public:
        Stub(const std::shared_ptr<::grpc::ChannelInterface>& channel);

<#list rpcList as rpc>
        <@stub_header_public rpc/>

</#list>
    private:
        std::shared_ptr<::grpc::ChannelInterface> channel_;
<#list rpcList as rpc>

        <@stub_header_private rpc/>
</#list>

<#list rpcList as rpc>
        const ::grpc::internal::RpcMethod rpcmethod_${rpc.name}_;
</#list>
    };

    static std::unique_ptr<Stub> NewStub(const std::shared_ptr<::grpc::ChannelInterface>& channel,
            const ::grpc::StubOptions& options = ::grpc::StubOptions());

    <#-- Server side - base -->
    class Service : public ::grpc::Service
    {
    public:
        Service();
        virtual ~Service();
<#list rpcList as rpc>
        <@service_header_public rpc/>
</#list>
    };

<#list rpcList as rpc>
    <#-- Server side - Asynchronous -->
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

        <@with_async_method_impl rpc, rpc?index/>

    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

</#list>
    <@typedef_async_service rpcList/>

<#list rpcList as rpc>
    <#-- Server side - Generic -->
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

        <@with_generic_method_impl rpc/>

    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

</#list>
<#list rpcList as rpc>
    <#-- Server side - Raw -->
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

        <@with_raw_method_impl rpc, rpc?index/>
    private:
        void BaseClassMustBeDerivedFromService(const Service* service) {}
    };

</#list>
<#list rpcList as rpc>
    <#-- Server side - Streamed Unary -->
    <#if rpc.noStreaming>
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

    </#if>
</#list>
    <@typedef_streamed_unary_service noStreamingRpcList/>

<#list rpcList as rpc>
    <#-- Server side - controlled server-side streaming -->
    <#if rpc.responseOnlyStreaming>
    template <class BaseClass>
    class WithSplitStreamingMethod_${rpc.name} : public BaseClass
    {
    public:
        WithSplitStreamingMethod_${rpc.name}()
        {
            typedef ::grpc::internal::SplitServerStreamingHandler<
                    ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}> SplitServerStreamingHandler;

            ::grpc::Service::MarkMethodStreamed(${rpc?index},
                    new SplitServerStreamingHandler(std::bind(
                            &WithSplitStreamingMethod_${rpc.name}<BaseClass>::Streamed${rpc.name},
                            this, std::placeholders::_1, std::placeholders::_2)));
        }

        ~WithSplitStreamingMethod_${rpc.name}() override
        {
            BaseClassMustBeDerivedFromService(this);
        }

        // disable regular version of this method
        ::grpc::Status ${rpc.name}(::grpc::ServerContext* context, const ${rpc.requestTypeFullName}* request,
                ::grpc::ServerWriter< ${rpc.responseTypeFullName}>* writer) override
        {
            abort();
            return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
        }

        // replace default version of method with split streamed
        virtual ::grpc::Status Streamed${rpc.name}(::grpc::ServerContext* context, ::grpc::ServerSplitStreamer<
                ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>* server_split_streamer) = 0;

    private:
            void BaseClassMustBeDerivedFromService(const Service *service) {}
    };

    </#if>
</#list>
    <@typedef_split_streamed_service responseOnlyStreamingRpcList/>

    <#-- Server side - typedef for controlled both unary and server-side streaming -->
    <@typedef_streamed_service noOrResponseOnlyStreamingRpcList/>
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
