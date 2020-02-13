<#include "FileHeader.inc.ftl">
<#include "Service.inc.ftl">
<@standard_header generatorDescription, packageName, [
        "java.io.ByteArrayInputStream",
        "java.io.InputStream",
        "java.io.IOException"
        "io.grpc.MethodDescriptor.Marshaller",
        "io.grpc.Status",
        "io.grpc.StatusRuntimeException",
        "com.google.common.io.ByteStreams",
        "zserio.runtime.io.ByteArrayBitStreamReader",
        "zserio.runtime.io.ByteArrayBitStreamWriter",
        "static io.grpc.MethodDescriptor.generateFullMethodName"
]/>

<#if hasNoStreamingRpc>
<@imports [
        "static io.grpc.stub.ClientCalls.asyncUnaryCall",
        "static io.grpc.stub.ClientCalls.blockingUnaryCall",
        "static io.grpc.stub.ClientCalls.futureUnaryCall",
        "static io.grpc.stub.ServerCalls.asyncUnaryCall"
]/>
</#if>
<#if hasRequestOnlyStreamingRpc>
<@imports [
        "static io.grpc.stub.ClientCalls.asyncClientStreamingCall",
        "static io.grpc.stub.ServerCalls.asyncClientStreamingCall"
]/>
</#if>
<#if hasResponseOnlyStreamingRpc>
<@imports [
        "static io.grpc.stub.ClientCalls.blockingServerStreamingCall",
        "static io.grpc.stub.ClientCalls.asyncServerStreamingCall",
        "static io.grpc.stub.ServerCalls.asyncServerStreamingCall"
]/>
</#if>
<#if hasBidiStreamingRpc>
<@imports [
        "static io.grpc.stub.ClientCalls.asyncBidiStreamingCall",
        "static io.grpc.stub.ServerCalls.asyncBidiStreamingCall"
]/>
</#if>
<#if hasNoStreamingRpc || hasResponseOnlyStreamingRpc>
<@imports [
        "static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall"
]/>
</#if>
<#if hasRequestOnlyStreamingRpc || hasBidiStreamingRpc>
<@imports [
        "static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall"
]/>
</#if>
<#assign servicePackagePrefix>
    <#if servicePackageName?has_content>${servicePackageName}.</#if><#t>
</#assign>

public final class ${className}
{
    private ${className}()
    {
    }

    public static final String SERVICE_NAME = "${servicePackagePrefix}${name}";

<#list rpcList as rpc>
    private static volatile <@method_descriptor rpc/> <@get_rpc_method rpc/>;

    @io.grpc.stub.annotations.RpcMethod(
            fullMethodName = SERVICE_NAME + '/' + "${rpc.name}",
            requestType = ${rpc.requestTypeFullName}.class,
            responseType = ${rpc.responseTypeFullName}.class,
            methodType = <@rpc_method_type rpc/>)
    public static <@method_descriptor rpc/> <@get_rpc_method rpc/>()
    {
        <@method_descriptor rpc/> <@get_rpc_method rpc/>;
        if ((<@get_rpc_method rpc/> = ${className}.<@get_rpc_method rpc/>) == null)
        {
            synchronized (${className}.class)
            {
                if ((<@get_rpc_method rpc/> = ${className}.<@get_rpc_method rpc/>) == null)
                {
                    Marshaller<${rpc.requestTypeFullName}> requestMarshaller =
                            <#lt><@marshaller rpc.requestTypeFullName, 7/>;

                    Marshaller<${rpc.responseTypeFullName}> responseMarshaller =
                            <#lt><@marshaller rpc.responseTypeFullName, 7/>;

                    ${className}.<@get_rpc_method rpc/> = <@get_rpc_method rpc/> =
                            <#lt><@method_descriptor_builder servicePackagePrefix, name, rpc, 7/>;
                }
            }
        }

        return <@get_rpc_method rpc/>;
    }

</#list>
    /**
    * Creates a new async stub that supports all call types for the service
    */
    public static ${name}Stub newStub(io.grpc.Channel channel)
    {
        return new ${name}Stub(channel);
    }

    /**
    * Creates a new blocking-style stub that supports unary and streaming output calls on the service
    */
    public static ${name}BlockingStub newBlockingStub(io.grpc.Channel channel)
    {
        return new ${name}BlockingStub(channel);
    }

    /**
    * Creates a new ListenableFuture-style stub that supports unary calls on the service
    */
    public static ${name}FutureStub newFutureStub(io.grpc.Channel channel)
    {
        return new ${name}FutureStub(channel);
    }

    public static abstract class ${name}ImplBase implements io.grpc.BindableService
    {
<#list rpcList as rpc>
        <@service_rpc_method rpc/>

</#list>
        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService()
        {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
<#list rpcList as rpc>
                    .addMethod(<@get_rpc_method rpc/>(), <@async_call_name rpc/>(
                            new MethodHandlers<@rpc_type_params rpc/>(
                                    this, METHODID_${rpc.name?upper_case})))
</#list>
                    .build();
        }
    }

    public static final class ${name}Stub extends io.grpc.stub.AbstractStub<${name}Stub>
    {
        private ${name}Stub(io.grpc.Channel channel)
        {
            super(channel);
        }

        private ${name}Stub(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected ${name}Stub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            return new ${name}Stub(channel, callOptions);
        }
<#list rpcList as rpc>

        <@stub_rpc_method rpc/>
</#list>
    }

    public static final class ${name}BlockingStub extends io.grpc.stub.AbstractStub<${name}BlockingStub>
    {
        private ${name}BlockingStub(io.grpc.Channel channel)
        {
            super(channel);
        }

        private ${name}BlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected ${name}BlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            return new ${name}BlockingStub(channel, callOptions);
        }
<#list rpcList as rpc>
        <@blocking_stub_rpc_method rpc/>
</#list>
    }

    public static final class ${name}FutureStub extends io.grpc.stub.AbstractStub<${name}FutureStub>
    {
        private ${name}FutureStub(io.grpc.Channel channel)
        {
            super(channel);
        }

        private ${name}FutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected ${name}FutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions)
        {
            return new ${name}FutureStub(channel, callOptions);
        }
<#list rpcList as rpc>
        <@future_stub_rpc_method rpc/>
</#list>
    }
<#list rpcList as rpc>

    private static final int METHODID_${rpc.name?upper_case} = ${rpc?index};
</#list>

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp>
    {
        private final ${name}ImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(${name}ImplBase serviceImpl, int methodId)
        {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
<#if hasNoStreamingRpc || hasResponseOnlyStreamingRpc>
        @java.lang.SuppressWarnings("unchecked")
</#if>
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver)
        {
            switch (methodId)
            {
<#list rpcList as rpc>
    <#if rpc.noStreaming || rpc.responseOnlyStreaming>
            case METHODID_${rpc.name?upper_case}:
                serviceImpl.${rpc.name}((${rpc.requestTypeFullName}) request,
                    (io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}>)responseObserver);
                break;
    </#if>
</#list>
            default:
                throw new AssertionError();
            }
        }

        @java.lang.Override
<#if hasRequestOnlyStreamingRpc || hasBidiStreamingRpc>
        @java.lang.SuppressWarnings("unchecked")
</#if>
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver)
        {
            switch (methodId)
            {
<#list rpcList as rpc>
    <#if rpc.requestOnlyStreaming || rpc.bidiStreaming>
            case METHODID_${rpc.name?upper_case}:
                return (io.grpc.stub.StreamObserver<Req>) serviceImpl.${rpc.name}(
                        (io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}>) responseObserver);
    </#if>
</#list>
            default:
                throw new AssertionError();
            }
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor()
    {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null)
        {
            synchronized (${className}.class)
            {
                result = serviceDescriptor;
                if (result == null)
                {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
<#list rpcList as rpc>
                            .addMethod(<@get_rpc_method rpc/>())
</#list>
                            .build();
                }
            }
        }

        return result;
    }
}
