package zserio_service_grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.14.0)",
    comments = "Source: zserio_service.proto")
public final class ZserioServiceGrpc {

  private ZserioServiceGrpc() {}

  public static final String SERVICE_NAME = "zserio_service_grpc.ZserioService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<zserio_service_grpc.ZserioServiceProto.Request,
      zserio_service_grpc.ZserioServiceProto.Response> getCallProcedureMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "callProcedure",
      requestType = zserio_service_grpc.ZserioServiceProto.Request.class,
      responseType = zserio_service_grpc.ZserioServiceProto.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<zserio_service_grpc.ZserioServiceProto.Request,
      zserio_service_grpc.ZserioServiceProto.Response> getCallProcedureMethod() {
    io.grpc.MethodDescriptor<zserio_service_grpc.ZserioServiceProto.Request, zserio_service_grpc.ZserioServiceProto.Response> getCallProcedureMethod;
    if ((getCallProcedureMethod = ZserioServiceGrpc.getCallProcedureMethod) == null) {
      synchronized (ZserioServiceGrpc.class) {
        if ((getCallProcedureMethod = ZserioServiceGrpc.getCallProcedureMethod) == null) {
          ZserioServiceGrpc.getCallProcedureMethod = getCallProcedureMethod = 
              io.grpc.MethodDescriptor.<zserio_service_grpc.ZserioServiceProto.Request, zserio_service_grpc.ZserioServiceProto.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "zserio_service_grpc.ZserioService", "callProcedure"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  zserio_service_grpc.ZserioServiceProto.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  zserio_service_grpc.ZserioServiceProto.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new ZserioServiceMethodDescriptorSupplier("callProcedure"))
                  .build();
          }
        }
     }
     return getCallProcedureMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ZserioServiceStub newStub(io.grpc.Channel channel) {
    return new ZserioServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ZserioServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ZserioServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ZserioServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ZserioServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ZserioServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void callProcedure(zserio_service_grpc.ZserioServiceProto.Request request,
        io.grpc.stub.StreamObserver<zserio_service_grpc.ZserioServiceProto.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getCallProcedureMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCallProcedureMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                zserio_service_grpc.ZserioServiceProto.Request,
                zserio_service_grpc.ZserioServiceProto.Response>(
                  this, METHODID_CALL_PROCEDURE)))
          .build();
    }
  }

  /**
   */
  public static final class ZserioServiceStub extends io.grpc.stub.AbstractStub<ZserioServiceStub> {
    private ZserioServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZserioServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZserioServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZserioServiceStub(channel, callOptions);
    }

    /**
     */
    public void callProcedure(zserio_service_grpc.ZserioServiceProto.Request request,
        io.grpc.stub.StreamObserver<zserio_service_grpc.ZserioServiceProto.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallProcedureMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ZserioServiceBlockingStub extends io.grpc.stub.AbstractStub<ZserioServiceBlockingStub> {
    private ZserioServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZserioServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZserioServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZserioServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public zserio_service_grpc.ZserioServiceProto.Response callProcedure(zserio_service_grpc.ZserioServiceProto.Request request) {
      return blockingUnaryCall(
          getChannel(), getCallProcedureMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ZserioServiceFutureStub extends io.grpc.stub.AbstractStub<ZserioServiceFutureStub> {
    private ZserioServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ZserioServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ZserioServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ZserioServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<zserio_service_grpc.ZserioServiceProto.Response> callProcedure(
        zserio_service_grpc.ZserioServiceProto.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getCallProcedureMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALL_PROCEDURE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ZserioServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ZserioServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL_PROCEDURE:
          serviceImpl.callProcedure((zserio_service_grpc.ZserioServiceProto.Request) request,
              (io.grpc.stub.StreamObserver<zserio_service_grpc.ZserioServiceProto.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ZserioServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ZserioServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return zserio_service_grpc.ZserioServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ZserioService");
    }
  }

  private static final class ZserioServiceFileDescriptorSupplier
      extends ZserioServiceBaseDescriptorSupplier {
    ZserioServiceFileDescriptorSupplier() {}
  }

  private static final class ZserioServiceMethodDescriptorSupplier
      extends ZserioServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ZserioServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ZserioServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ZserioServiceFileDescriptorSupplier())
              .addMethod(getCallProcedureMethod())
              .build();
        }
      }
    }
    return result;
  }
}
