<#macro get_rpc_method rpc>
    get${rpc.name?cap_first}Method<#t>
</#macro>

<#macro rpc_type_params rpc>
    <${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}><#t>
</#macro>

<#macro method_descriptor rpc>
    io.grpc.MethodDescriptor<@rpc_type_params rpc/><#t>
</#macro>

<#macro method_descriptor_builder packagePrefix service rpc indent >
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}io.grpc.MethodDescriptor.<@rpc_type_params rpc/>newBuilder()
${I}        .setType(<@rpc_method_type rpc/>)
${I}        .setFullMethodName(generateFullMethodName(
${I}                "${packagePrefix}${service}", "${rpc.name}"))
${I}        .setSampledToLocalTracing(true)
${I}        .setRequestMarshaller(requestMarshaller)
${I}        .setResponseMarshaller(responseMarshaller)
${I}        .build()<#rt>
</#macro>

<#macro marshaller typeFullName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}new Marshaller<${typeFullName}>()
${I}{
${I}    @Override
${I}    public ${typeFullName} parse(InputStream is)
${I}    {
${I}        try
${I}        {
${I}            byte[] bytes = ByteStreams.toByteArray(is);
${I}            ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
${I}            return new ${typeFullName}(reader);
${I}        }
${I}        catch (IOException e)
${I}        {
${I}            throw new StatusRuntimeException(Status.DATA_LOSS);
${I}        }
${I}    }
${I}    @Override
${I}    public InputStream stream(${typeFullName} request)
${I}    {
${I}        try
${I}        {
${I}            ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
${I}            request.write(writer);
${I}            byte[] bytes = writer.toByteArray();
${I}            return new ByteArrayInputStream(bytes);
${I}        }
${I}        catch (IOException e)
${I}        {
${I}            throw new StatusRuntimeException(Status.DATA_LOSS);
${I}        }
${I}    }
${I}}<#rt>
</#macro>

<#macro rpc_method_type rpc>
    <#if rpc.noStreaming>
        io.grpc.MethodDescriptor.MethodType.UNARY<#t>
    <#elseif rpc.requestOnlyStreaming>
        io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING<#t>
    <#elseif rpc.responseOnlyStreaming>
        io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING<#t>
    <#else><#-- bidi streaming -->
        io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING<#t>
    </#if>
</#macro>

<#macro service_rpc_method rpc>
    <#if rpc.noStreaming || rpc.responseOnlyStreaming>
        public void ${rpc.name}(${rpc.requestTypeFullName} request,
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            asyncUnimplementedUnaryCall(<@get_rpc_method rpc/>(), responseObserver);
        }
    <#else><#-- rpc.requestOnlyStreaming || bidi streaming -->
        public io.grpc.stub.StreamObserver<${rpc.requestTypeFullName}> ${rpc.name}(
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            return asyncUnimplementedStreamingCall(<@get_rpc_method rpc/>(), responseObserver);
        }
    </#if>
</#macro>

<#macro async_call_name rpc>
    <#if rpc.noStreaming>
        asyncUnaryCall<#t>
    <#elseif rpc.requestOnlyStreaming>
        asyncClientStreamingCall<#t>
    <#elseif rpc.responseOnlyStreaming>
        asyncServerStreamingCall<#t>
    <#else><#-- bidi streaming -->
        asyncBidiStreamingCall<#t>
    </#if>
</#macro>

<#macro stub_rpc_method rpc>
    <#if rpc.noStreaming>
        public void ${rpc.name}(${rpc.requestTypeFullName} request,
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            asyncUnaryCall(getChannel().newCall(
                    <@get_rpc_method rpc/>(), getCallOptions()), request, responseObserver);
        }
    <#elseif rpc.requestOnlyStreaming>
        public io.grpc.stub.StreamObserver<${rpc.requestTypeFullName}> ${rpc.name}(
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            return asyncClientStreamingCall(getChannel().newCall(
                <@get_rpc_method rpc/>(), getCallOptions()), responseObserver);
        }
    <#elseif rpc.responseOnlyStreaming>
        public void ${rpc.name}(${rpc.requestTypeFullName} request,
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            asyncServerStreamingCall(getChannel().newCall(
                    <@get_rpc_method rpc/>(), getCallOptions()), request, responseObserver);
        }
    <#else><#-- bidi streaming -->
        public io.grpc.stub.StreamObserver<${rpc.requestTypeFullName}> ${rpc.name}(
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            return asyncBidiStreamingCall(getChannel().newCall(
                <@get_rpc_method rpc/>(), getCallOptions()), responseObserver);
        }
    </#if>
</#macro>

<#macro blocking_stub_rpc_method rpc>
    <#if rpc.noStreaming>

        public ${rpc.responseTypeFullName} ${rpc.name}(${rpc.requestTypeFullName} request)
        {
            return blockingUnaryCall(getChannel(), <@get_rpc_method rpc/>(), getCallOptions(), request);
        }
    <#elseif rpc.responseOnlyStreaming>

        public java.util.Iterator<${rpc.responseTypeFullName}> ${rpc.name}(
                ${rpc.requestTypeFullName} request)
        {
            return blockingServerStreamingCall(
                    getChannel(), <@get_rpc_method rpc/>(), getCallOptions(), request);
        }
    </#if>
</#macro>

<#macro future_stub_rpc_method rpc>
    <#if rpc.noStreaming>

        public com.google.common.util.concurrent.ListenableFuture<${rpc.responseTypeFullName}> ${rpc.name}(
                ${rpc.requestTypeFullName} request)
        {
            return futureUnaryCall(getChannel().newCall(<@get_rpc_method rpc/>(), getCallOptions()), request);
        }
    </#if>
</#macro>
