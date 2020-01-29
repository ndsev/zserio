package zserio_service_grpc;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import zserio_runtime.ServiceInterface;

public class ZserioService
{
    public static class GrpcService extends ZserioServiceGrpc.ZserioServiceImplBase
    {
        public GrpcService(ServiceInterface service)
        {
            this.service = service;
        }

        @Override
        public void callProcedure(ZserioServiceProto.Request request,
                StreamObserver<ZserioServiceProto.Response> responseObserver)
        {
            final byte[] responseData = service.callProcedure(
                    request.getProcName(), request.getRequestData().toByteArray());
            final ZserioServiceProto.Response response =
                    ZserioServiceProto.Response.newBuilder()
                            .setResponseData(ByteString.copyFrom(responseData)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private final ServiceInterface service;
    }

    public static class GrpcClient implements ServiceInterface
    {
        public GrpcClient(Channel channel)
        {
            stub = ZserioServiceGrpc.newBlockingStub(channel);
        }

        @Override
        public byte[] callProcedure(String procName, byte[] requestData)
        {
            final ZserioServiceProto.Request request =
                    ZserioServiceProto.Request.newBuilder()
                            .setProcName(procName)
                            .setRequestData(ByteString.copyFrom(requestData)).build();
            final ZserioServiceProto.Response response = stub.callProcedure(request);

            return response.getResponseData().toByteArray();
        }

        private final ZserioServiceGrpc.ZserioServiceBlockingStub stub;
    }
}
