<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path package.path, "${name}.h"/>"

#include "<@include_path rootPackage.path, "GrpcSerializationTraits.h"/>"

<@user_includes cppUserIncludes, true/>
#include <grpcpp/impl/codegen/async_stream.h>
#include <grpcpp/impl/codegen/async_unary_call.h>
#include <grpcpp/impl/codegen/channel_interface.h>
#include <grpcpp/impl/codegen/client_unary_call.h>
#include <grpcpp/impl/codegen/method_handler_impl.h>
#include <grpcpp/impl/codegen/rpc_service_method.h>
#include <grpcpp/impl/codegen/service_type.h>
#include <grpcpp/impl/codegen/sync_stream.h>

<@namespace_begin package.path/>

static const char* ${name}_method_names[] =
{
<#list rpcList as rpc>
    "/<#if package.name?has_content>${package.name}.</#if>${name}/${rpc.name}"<#if rpc?has_next>,</#if>
</#list>
};

std::unique_ptr<${name}::Stub> ${name}::NewStub(const std::shared_ptr<::grpc::ChannelInterface>& channel,
        const ::grpc::StubOptions& options)
{
    (void) options;
    std::unique_ptr< ${name}::Stub> stub(new ${name}::Stub(channel));
    return stub;
}

${name}::Stub::Stub(const std::shared_ptr< ::grpc::ChannelInterface>& channel) :
        channel_(channel)<#if rpcList?has_content>,</#if>
<#list rpcList as rpc>
        rpcmethod_${rpc.name}(${name}_method_names[${rpc?index}],
                ::grpc::internal::RpcMethod::NORMAL_RPC, channel)<#rt>
        <#if rpc?is_last>

        <#else>
            <#lt>,
        </#if>
</#list>
{
}

<#list rpcList as rpc>
::grpc::Status ${name}::Stub::${rpc.name}(::grpc::ClientContext* context,
        const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response)
{
    return ::grpc::internal::BlockingUnaryCall(channel_.get(),
            rpcmethod_${rpc.name}, context, request, response);
}

${name}::Stub::ClientAsync${rpc.name}Reader* ${name}::Stub::Async${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncResponseReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}, context, request, true);
}

${name}::Stub::ClientAsync${rpc.name}Reader* ${name}::Stub::PrepareAsync${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncResponseReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}, context, request, false);
}

</#list>
${name}::Service::Service()
{
<#list rpcList as rpc>
    AddMethod(new ::grpc::internal::RpcServiceMethod(
            ${name}_method_names[${rpc?index}], ::grpc::internal::RpcMethod::NORMAL_RPC,
            new ::grpc::internal::RpcMethodHandler<
                    ${name}::Service, ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>(
                            std::mem_fn(&${name}::Service::${rpc.name}), this)));
</#list>
}

${name}::Service::~Service()
{
}

<#list rpcList as rpc>
::grpc::Status ${name}::Service::${rpc.name}(::grpc::ServerContext* context,
        const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName} * response)
{
    (void) context;
    (void) request;
    (void) response;
    return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
</#list>

<@namespace_end package.path/>
