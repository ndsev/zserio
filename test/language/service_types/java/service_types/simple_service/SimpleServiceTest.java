package service_types.simple_service;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.service.ServiceException;

public class SimpleServiceTest
{
    @Test
    public void serviceFullName()
    {
        assertEquals("service_types.simple_service.SimpleService", SimpleService.Service.SERVICE_FULL_NAME);
    }

    @Test
    public void methodNames()
    {
        assertEquals("powerOfTwo", SimpleService.Service.METHOD_NAMES[0]);
    }

    @Test
    public void powerOfTwo() throws IOException
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

    @Test(expected=ServiceException.class)
    public void invalidServiceMethod() throws IOException
    {
        service.callMethod("nonexistentMethod", null, null);
    }

    @Test
    public void callWithContext() throws IOException
    {
        final FakeContext fakeContext = new FakeContext();
        assertFalse(fakeContext.seenByService);
        final Request request = new Request(10);
        final Response response = client.powerOfTwoMethod(request, fakeContext);
        assertEquals(BigInteger.valueOf(100), response.getValue());
        assertTrue(fakeContext.seenByService);
    }

    private static class Service extends SimpleService.Service
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
    private static final SimpleService.Client client = new SimpleService.Client(service);
}
