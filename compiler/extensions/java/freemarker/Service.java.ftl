<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.io.ByteArrayInputStream",
        "java.io.InputStream",
        "java.io.IOException"
        "io.grpc.MethodDescriptor.Marshaller",
        "io.grpc.Status",
        "io.grpc.StatusRuntimeException",
        "static io.grpc.MethodDescriptor.generateFullMethodName",
        "static io.grpc.stub.ClientCalls.asyncUnaryCall",
        "static io.grpc.stub.ClientCalls.blockingUnaryCall",
        "static io.grpc.stub.ClientCalls.futureUnaryCall",
        "static io.grpc.stub.ServerCalls.asyncUnaryCall",
        "static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall",
        "com.google.common.io.ByteStreams",
        "com.google.common.util.concurrent.ListenableFuture",
        "zserio.runtime.io.ByteArrayBitStreamReader",
        "zserio.runtime.io.ByteArrayBitStreamWriter"
]/>
<#assign packagePrefix>
    <#if packageName?has_content>${packageName}.</#if><#t>
</#assign>
<#macro getRpcMethod rpc>
    get${rpc.name?cap_first}Method<#t>
</#macro>
<#macro rpcTypeParams rpc>
    <${rpc.requestTypeFullName}, ${rpc.responseTypeFullName}><#t>
</#macro>
<#macro methodDescriptor rpc>
    io.grpc.MethodDescriptor<@rpcTypeParams rpc/><#t>
</#macro>
<#macro methodDescriptorBuilder rpc>
    io.grpc.MethodDescriptor.<@rpcTypeParams rpc/>newBuilder()<#t>
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

<@class_header generatorDescription/>
public final class ${className}
{
    private ${className}()
    {
    }

    public static final String SERVICE_NAME = "${packagePrefix}${name}";

<#list rpcList as rpc>
    private static volatile <@methodDescriptor rpc/> <@getRpcMethod rpc/>;

    @io.grpc.stub.annotations.RpcMethod(
            fullMethodName = SERVICE_NAME + '/' + "${rpc.name}",
            requestType = ${rpc.requestTypeFullName}.class,
            responseType = ${rpc.responseTypeFullName}.class,
            methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static <@methodDescriptor rpc/> <@getRpcMethod rpc/>()
    {
        <@methodDescriptor rpc/> <@getRpcMethod rpc/>;
        if ((<@getRpcMethod rpc/> = ${className}.<@getRpcMethod rpc/>) == null)
        {
            synchronized (${className}.class)
            {
                if ((<@getRpcMethod rpc/> = ${className}.<@getRpcMethod rpc/>) == null)
                {
                    Marshaller<${rpc.requestTypeFullName}> requestMarshaller =
                            <#lt><@marshaller rpc.requestTypeFullName, 7/>;

                    Marshaller<${rpc.responseTypeFullName}> responseMarshaller =
                            <#lt><@marshaller rpc.responseTypeFullName, 7/>;

                    ${className}.<@getRpcMethod rpc/> = <@getRpcMethod rpc/> = <@methodDescriptorBuilder rpc/>
                            .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                            .setFullMethodName(generateFullMethodName(
                                    "${packagePrefix}${name}", "${rpc.name}"))
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(requestMarshaller)
                            .setResponseMarshaller(responseMarshaller)
                            .build();
                }
            }
        }

        return <@getRpcMethod rpc/>;
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
        public void ${rpc.name}(${rpc.requestTypeFullName} request,
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            asyncUnimplementedUnaryCall(<@getRpcMethod rpc/>(), responseObserver);
        }

</#list>
        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService()
        {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
<#list rpcList as rpc>
                    .addMethod(<@getRpcMethod rpc/>(), asyncUnaryCall(new MethodHandlers<@rpcTypeParams rpc/>(
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

        public void ${rpc.name}(${rpc.requestTypeFullName} request,
                io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}> responseObserver)
        {
            asyncUnaryCall(getChannel().newCall(<@getRpcMethod rpc/>(), getCallOptions()),
                    request, responseObserver);
        }
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

        public ${rpc.responseTypeFullName} ${rpc.name}(${rpc.requestTypeFullName} request)
        {
            return blockingUnaryCall(getChannel(), <@getRpcMethod rpc/>(), getCallOptions(), request);
        }
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

        public ListenableFuture<${rpc.responseTypeFullName}> ${rpc.name}(${rpc.requestTypeFullName} request)
        {
            return futureUnaryCall(getChannel().newCall(<@getRpcMethod rpc/>(), getCallOptions()), request);
        }
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
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver)
        {
            switch (methodId)
            {
<#list rpcList as rpc>
            case METHODID_${rpc.name?upper_case}:
                serviceImpl.${rpc.name}((${rpc.requestTypeFullName}) request,
                    (io.grpc.stub.StreamObserver<${rpc.responseTypeFullName}>)responseObserver);
                break;
</#list>
            default:
                throw new AssertionError();
            }
        }

        @java.lang.Override
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver)
        {
            switch (methodId)
            {
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
                            .addMethod(<@getRpcMethod rpc/>())
</#list>
                            .build();
                }
            }
        }

        return result;
    }
}
