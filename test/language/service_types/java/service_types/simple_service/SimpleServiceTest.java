package service_types.simple_service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.Writer;
import zserio.runtime.service.ServiceData;
import zserio.runtime.service.ServiceException;

public class SimpleServiceTest
{
    @Test
    public void serviceFullName()
    {
        assertEquals("service_types.simple_service.SimpleService",
                SimpleService.SimpleServiceService.serviceFullName());
    }

    @Test
    public void methodNames()
    {
        assertEquals("powerOfTwo", SimpleService.powerOfTwo_METHOD_NAME);
        assertEquals("powerOfTwo", SimpleService.SimpleServiceService.methodNames()[0]);
    }

    @Test
    public void powerOfTwo()
    {
        final Request request = new Request(13);
        assertEquals(BigInteger.valueOf(169), client.powerOfTwoMethod(request).getValue());
        request.setValue(-13);
        assertEquals(BigInteger.valueOf(169), client.powerOfTwoMethod(request).getValue());
        request.setValue(2);
        assertEquals(BigInteger.valueOf(4), client.powerOfTwoMethod(request).getValue());
        request.setValue(-2);
        assertEquals(BigInteger.valueOf(4), client.powerOfTwoMethod(request).getValue());
    }

    @Test
    public void powerOfTwoRaw() throws IOException
    {
        final Request request = new Request(13);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        request.write(writer);
        final byte[] requestData = writer.toByteArray();
        final byte[] responseData = client.powerOfTwoRawMethod(requestData);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(responseData);
        final Response response = new Response(reader);
        assertEquals(BigInteger.valueOf(169), response.getValue());
    }

    @Test
    public void invalidServiceMethod()
    {
        assertThrows(ServiceException.class, () -> service.callMethod("nonexistentMethod", null, null));
    }

    @Test
    public void callWithContext()
    {
        final FakeContext fakeContext = new FakeContext();
        assertFalse(fakeContext.seenByService);
        final Request request = new Request(10);
        final Response response = client.powerOfTwoMethod(request, fakeContext);
        assertEquals(BigInteger.valueOf(100), response.getValue());
        assertTrue(fakeContext.seenByService);
    }

    private static class Service extends SimpleService.SimpleServiceService
    {
        @Override
        public Response powerOfTwoImpl(Request request, Object context)
        {
            if (context != null)
                ((FakeContext)context).seenByService = true;

            final Response response = new Response(BigInteger.valueOf(request.getValue()).pow(2));
            return response;
        }

        @Override
        public byte[] powerOfTwoRawImpl(byte[] requestData, Object context)
        {
            try
            {
                final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(requestData);
                final Request request = new Request(reader);
                final Response response = powerOfTwoImpl(request, context);
                final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
                response.write(writer);
                return writer.toByteArray();
            }
            catch (IOException e)
            {
                return new byte[] {};
            }
        }
    }

    private static class FakeContext
    {
        public boolean seenByService = false;
    }

    private static class LocalServiceClient implements zserio.runtime.service.ServiceClientInterface
    {
        public LocalServiceClient(Service service)
        {
            this.service = service;
        }

        @Override
        public byte[] callMethod(String methodName, ServiceData<? extends Writer> request, Object context)
        {
            return service.callMethod(methodName, request.getByteArray(), context).getByteArray();
        }

        private final Service service;
    }

    private static final Service service = new Service();
    private static final LocalServiceClient localServiceClient = new LocalServiceClient(service);
    private static final SimpleService.SimpleServiceClient client =
            new SimpleService.SimpleServiceClient(localServiceClient);
}
