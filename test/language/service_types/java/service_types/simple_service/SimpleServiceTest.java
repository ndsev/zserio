package service_types.simple_service;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

public class SimpleServiceTest
{
    @BeforeClass
    public static void init() throws IOException
    {
        final String serviceName = "SimpleService";
        server = new SimpleServiceServer(serviceName);
        server.start();
        client = new SimpleServiceClient(serviceName);
    }

    @AfterClass
    public static void shutdown()
    {
        server.shutdown();
    }

    @Test
    public void powerOfTwo()
    {
        assertEquals(BigInteger.valueOf(169), client.powerOfTwo(13));
        assertEquals(BigInteger.valueOf(169), client.powerOfTwo(-13));
        assertEquals(BigInteger.valueOf(4), client.powerOfTwo(2));
        assertEquals(BigInteger.valueOf(4), client.powerOfTwo(-2));
    }

    @Test
    public void powerOfTwoAsync() throws InterruptedException
    {
        assertEquals(BigInteger.valueOf(169), client.powerOfTwoAsync(13));
        assertEquals(BigInteger.valueOf(169), client.powerOfTwoAsync(-13));
        assertEquals(BigInteger.valueOf(4), client.powerOfTwoAsync(2));
        assertEquals(BigInteger.valueOf(4), client.powerOfTwoAsync(-2));
    }

    private static class SimpleServiceClient
    {
        public SimpleServiceClient(String name)
        {
            channel = InProcessChannelBuilder.forName(name).build();
            blockingStub = SimpleServiceGrpc.newBlockingStub(channel);
            asyncStub = SimpleServiceGrpc.newStub(channel);
        }

        public BigInteger powerOfTwo(int value)
        {
            Request request = new Request();
            request.setValue(value);

            Response response;
            try
            {
                response = blockingStub.powerOfTwo(request);
                return response.getValue();
            }
            catch (StatusRuntimeException e)
            {
                System.out.println(e.getStatus());
                return BigInteger.ZERO;
            }
        }

        public BigInteger powerOfTwoAsync(int value) throws InterruptedException
        {
            Request request = new Request();
            request.setValue(value);

            final BigInteger[] result = new BigInteger[1];

            final CountDownLatch finishLatch = new CountDownLatch(1);
            StreamObserver<Response> responseObserver = new StreamObserver<Response>() {
                @Override
                public void onCompleted()
                {
                    finishLatch.countDown();
                }
                @Override
                public void onError(Throwable t)
                {
                    fail(Status.fromThrowable(t).getDescription());
                }
                @Override
                public void onNext(Response response)
                {
                    result[0] = response.getValue();
                }
            };

            asyncStub.powerOfTwo(request, responseObserver);
            assertTrue(finishLatch.await(5, TimeUnit.SECONDS));
            return result[0];
        }

        private final ManagedChannel channel;
        private final SimpleServiceGrpc.SimpleServiceBlockingStub blockingStub;
        private final SimpleServiceGrpc.SimpleServiceStub asyncStub;
    }

    private static class SimpleService extends SimpleServiceGrpc.SimpleServiceImplBase
    {
        @Override
        public void powerOfTwo(Request request, StreamObserver<Response> responseObserver)
        {
            Response response = new Response();
            response.setValue(BigInteger.valueOf(request.getValue()).pow(2));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    private static class SimpleServiceServer
    {
        public SimpleServiceServer(String name)
        {
            server = InProcessServerBuilder.forName(name).addService(new SimpleService()).build();
        }

        public void start() throws IOException
        {
            server.start();
        }

        public void shutdown()
        {
            server.shutdown();
        }

        private final Server server;
    }

    private static SimpleServiceServer server;
    private static SimpleServiceClient client;
}
