package service_types.simple_service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import zserio.runtime.service.ServiceData;
import zserio.runtime.service.ServiceException;
import zserio.runtime.io.Writer;

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
        public <T extends Writer> byte[] callMethod(String methodName, ServiceData<T> request, Object context)
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
