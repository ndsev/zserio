package service_poc;

import java.lang.RuntimeException;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import zserio_runtime.ServiceInterface;

public final class SimpleService
{
    public static abstract class Service implements ServiceInterface
    {
        public Service()
        {
            procedureMap = new HashMap<String, MethodReference>();
            procedureMap.put("SimpleService.powerOfTwo", new MethodReference() {
                public byte[] call(byte[] requestData)
                {
                    return powerOfTwoCall(requestData);
                }
            });
            procedureMap.put("SimpleService.powerOfFour", new MethodReference() {
                public byte[] call(byte[] requestData)
                {
                    return powerOfFourCall(requestData);
                }
            });
        }

        @Override
        public byte[] callProcedure(String procName, byte[] requestData)
        {
            // TODO: add check that method exists
            MethodReference method = procedureMap.get(procName);
            return method.call(requestData);
        }

        private interface MethodReference
        {
            byte[] call(byte[] requestData);
        }

        private byte[] powerOfTwoCall(byte[] requestData)
        {
            try
            {
                final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(requestData);
                final Request request = new Request(reader);

                final Response response = powerOfTwoImpl(request);

                final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
                response.write(writer);
                return writer.toByteArray();
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                    "SimpleService.Service.powerOfTwoCall failed (" + e.getMessage() + ")!");
            }
        }

        private byte[] powerOfFourCall(byte[] requestData)
        {
            try
            {
                final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(requestData);
                final Request request = new Request(reader);

                final Response response = powerOfFourImpl(request);

                final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
                response.write(writer);
                return writer.toByteArray();
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                    "SimpleService.Service.powerOfTwoCall failed (" + e.getMessage() + ")!");
            }
        }

        protected abstract Response powerOfTwoImpl(Request request);
        protected abstract Response powerOfFourImpl(Request request);

        private final Map<String, MethodReference> procedureMap;
    }

    public static final class Client
    {
        public Client(ServiceInterface service)
        {
            this.service = service;
        }

        public Response callPowerOfTwo(Request request) throws IOException
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            request.write(writer);
            final byte[] requestData = writer.toByteArray();

            final byte[] responseData = service.callProcedure("SimpleService.powerOfTwo", requestData);

            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(responseData);
            final Response response = new Response(reader);
            return response;
        }

        public Response callPowerOfFour(Request request) throws IOException
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            request.write(writer);
            final byte[] requestData = writer.toByteArray();

            final byte[] responseData = service.callProcedure("SimpleService.powerOfFour", requestData);

            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(responseData);
            final Response response = new Response(reader);
            return response;
        }

        private final ServiceInterface service;
    }
}
