package service_types.simple_service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

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

    private static final Service service = new Service();
    private static final SimpleService.SimpleServiceClient client =
            new SimpleService.SimpleServiceClient(service);
}
