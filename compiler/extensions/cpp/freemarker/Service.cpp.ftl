<#include "FileHeader.inc.ftl">
<#include "Service.cpp.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path package.path, "${name}.h"/>"

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
    std::unique_ptr<${name}::Stub> stub(new ${name}::Stub(channel));
    return stub;
}

${name}::Stub::Stub(const std::shared_ptr<::grpc::ChannelInterface>& channel) :
        channel_(channel)<#if rpcList?has_content>,</#if>
<#list rpcList as rpc>
        rpcmethod_${rpc.name}_(${name}_method_names[${rpc?index}],
                <@rpc_method_type rpc/>, channel)<#rt>
        <#if rpc?is_last>

        <#else>
            <#lt>,
        </#if>
</#list>
{
}

<#list rpcList as rpc>
<@stub_source_public name, rpc/>

</#list>
${name}::Service::Service()
{
<#list rpcList as rpc>
    AddMethod(new ::grpc::internal::RpcServiceMethod(
            ${name}_method_names[${rpc?index}], <@rpc_method_type rpc/>,
            new <@rpc_method_handler rpc/><
                    ${name}::Service, ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>(
                            std::mem_fn(&${name}::Service::${rpc.name}), this)));
</#list>
}

${name}::Service::~Service()
{
}

<#list rpcList as rpc>
<@service_unimplemented_method name, rpc/>
</#list>

<@namespace_end package.path/>
