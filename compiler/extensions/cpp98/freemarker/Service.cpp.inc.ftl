<#macro rpc_method_type rpc>
    <#if rpc.noStreaming>
        ::grpc::internal::RpcMethod::NORMAL_RPC<#t>
    <#elseif rpc.requestOnlyStreaming>
        ::grpc::internal::RpcMethod::CLIENT_STREAMING<#t>
    <#elseif rpc.responseOnlyStreaming>
        ::grpc::internal::RpcMethod::SERVER_STREAMING<#t>
    <#else><#-- bidi streaming -->
        ::grpc::internal::RpcMethod::BIDI_STREAMING<#t>
    </#if>
</#macro>

<#macro rpc_method_handler rpc>
    <#if rpc.noStreaming>
        ::grpc::internal::RpcMethodHandler<#t>
    <#elseif rpc.requestOnlyStreaming>
        ::grpc::internal::ClientStreamingHandler<#t>
    <#elseif rpc.responseOnlyStreaming>
        ::grpc::internal::ServerStreamingHandler<#t>
    <#else><#-- bidi streaming -->
        ::grpc::internal::BidiStreamingHandler<#t>
    </#if>
</#macro>

<#macro stub_source_public service rpc>
    <#if rpc.noStreaming>
::grpc::Status ${service}::Stub::${rpc.name}(::grpc::ClientContext* context,
        const ${rpc.requestTypeFullName}& request, ${rpc.responseTypeFullName}* response)
{
    return ::grpc::internal::BlockingUnaryCall(channel_.get(),
            rpcmethod_${rpc.name}_, context, request, response);
}

${service}::Stub::ClientAsync${rpc.name}Reader* ${service}::Stub::Async${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncResponseReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, request, true);
}

${service}::Stub::ClientAsync${rpc.name}Reader* ${service}::Stub::PrepareAsync${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncResponseReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, request, false);
}
    <#elseif rpc.requestOnlyStreaming>
${service}::Stub::Client${rpc.name}Writer* ${service}::Stub::${rpc.name}Raw(::grpc::ClientContext* context,
        ${rpc.responseTypeFullName}* response)
{
    return ::grpc::internal::ClientWriterFactory<${rpc.requestTypeFullName}>::Create(
            channel_.get(), rpcmethod_${rpc.name}_, context, response);
}

${service}::Stub::ClientAsync${rpc.name}Writer* ${service}::Stub::Async${rpc.name}Raw(
        ::grpc::ClientContext* context, ${rpc.responseTypeFullName}* response,
        ::grpc::CompletionQueue* cq, void* tag)
{
    return ::grpc::internal::ClientAsyncWriterFactory<${rpc.requestTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, response, true, tag);
}

${service}::Stub::ClientAsync${rpc.name}Writer* ${service}::Stub::PrepareAsync${rpc.name}Raw(
        ::grpc::ClientContext* context, ${rpc.responseTypeFullName}* response, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncWriterFactory<${rpc.requestTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, response, false, nullptr);
}
    <#elseif rpc.responseOnlyStreaming>
${service}::Stub::Client${rpc.name}Reader* ${service}::Stub::${rpc.name}Raw(::grpc::ClientContext* context,
        const ${rpc.requestTypeFullName}& request)
{
    return ::grpc::internal::ClientReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), rpcmethod_${rpc.name}_, context, request);
}

${service}::Stub::ClientAsync${rpc.name}Reader* ${service}::Stub::Async${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request,
        ::grpc::CompletionQueue* cq, void* tag)
{
    return ::grpc::internal::ClientAsyncReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, request, true, tag);
}

${service}::Stub::ClientAsync${rpc.name}Reader* ${service}::Stub::PrepareAsync${rpc.name}Raw(
        ::grpc::ClientContext* context, const ${rpc.requestTypeFullName}& request, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncReaderFactory<${rpc.responseTypeFullName}>::Create(
            channel_.get(), cq, rpcmethod_${rpc.name}_, context, request, false, nullptr);
}
    <#else><#-- bidi streaming -->
${service}::Stub::Client${rpc.name}ReaderWriter* ${service}::Stub::${rpc.name}Raw(::grpc::ClientContext* context)
{
    return ::grpc::internal::ClientReaderWriterFactory<
            ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>::Create(
                    channel_.get(), rpcmethod_${rpc.name}_, context);
}

${service}::Stub::ClientAsync${rpc.name}ReaderWriter* ${service}::Stub::Async${rpc.name}Raw(
        ::grpc::ClientContext* context, ::grpc::CompletionQueue* cq, void* tag)
{
    return ::grpc::internal::ClientAsyncReaderWriterFactory<
            ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>::Create(
                    channel_.get(), cq, rpcmethod_${rpc.name}_, context, true, tag);
}

${service}::Stub::ClientAsync${rpc.name}ReaderWriter* ${service}::Stub::PrepareAsync${rpc.name}Raw(
        ::grpc::ClientContext* context, ::grpc::CompletionQueue* cq)
{
    return ::grpc::internal::ClientAsyncReaderWriterFactory<
            ${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}>::Create(
                    channel_.get(), cq, rpcmethod_${rpc.name}_, context, false, nullptr);
}
    </#if>
</#macro>

<#macro service_unimplemented_method service rpc>
    <#if rpc.noStreaming>
::grpc::Status ${service}::Service::${rpc.name}(::grpc::ServerContext* context,
        const ${rpc.requestTypeFullName}* request, ${rpc.responseTypeFullName}* response)
{
    (void) context;
    (void) request;
    (void) response;
    return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
    <#elseif rpc.requestOnlyStreaming>
::grpc::Status ${service}::Service::${rpc.name}(::grpc::ServerContext* context,
        ::grpc::ServerReader<${rpc.requestTypeFullName}>* reader, ${rpc.responseTypeFullName}* response)
{
    (void) context;
    (void) reader;
    (void) response;
    return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
    <#elseif rpc.responseOnlyStreaming>
::grpc::Status ${service}::Service::${rpc.name}(::grpc::ServerContext* context,
        const ${rpc.requestTypeFullName}* request, ::grpc::ServerWriter<${rpc.responseTypeFullName}>* writer)
{
    (void) context;
    (void) request;
    (void) writer;
    return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
    <#else><#-- bidi streaming -->
::grpc::Status ${service}::Service::${rpc.name}(::grpc::ServerContext* context,
        ::grpc::ServerReaderWriter<${rpc.responseTypeFullName}, ${rpc.requestTypeFullName}>* stream)
{
    (void) context;
    (void) stream;
    return ::grpc::Status(::grpc::StatusCode::UNIMPLEMENTED, "");
}
    </#if>
</#macro>
