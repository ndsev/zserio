<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.io.IOException",
        "java.util.HashMap",
        "java.util.Map",
        "zserio.runtime.io.ByteArrayBitStreamReader",
        "zserio.runtime.io.ByteArrayBitStreamWriter"
        "zserio.runtime.service.ServiceInterface",
        "zserio.runtime.service.ServiceException"
]/>

public final class ${name}
{
    public static abstract class Service implements ServiceInterface
    {
        public Service()
        {
            methodMap = new HashMap<String, Method>();
<#list methodList as method>
            methodMap.put("${method.name}",
                new Method()
                {
                    public byte[] call(byte[] requestData, Object context) throws IOException
                    {
                        return ${method.name}Method(requestData, context);
                    }
                }
            );
</#list>
        }

        @Override
        public byte[] callMethod(String methodName, byte[] requestData, Object context) throws IOException
        {
            final Method method = methodMap.get(methodName);
            if (method == null)
                throw new ServiceException("${serviceFullName}: Method '" + methodName + "' does not exist!");
            return method.call(requestData, context);
        }

        public static final String SERVICE_FULL_NAME = "${serviceFullName}";
        public static final String[] METHOD_NAMES = {
<#list methodList as method>
            "${method.name}"<#if method?has_next>,</#if>
</#list>
        };
<#list methodList as method>

        protected abstract ${method.responseTypeFullName} ${method.name}Impl(<#rt>
                <#lt>${method.requestTypeFullName} request, Object context);
</#list>
<#list methodList as method>

        private byte[] ${method.name}Method(byte[] requestData, Object context)
                throws IOException, ServiceException
        {
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(requestData);
            final ${method.requestTypeFullName} request = new Request(reader);

            final ${method.responseTypeFullName} response = ${method.name}Impl(request, context);

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            response.write(writer);
            return writer.toByteArray();
        }
</#list>

        private interface Method
        {
            byte[] call(byte[] requestData, Object context) throws IOException;
        }

        private final Map<String, Method> methodMap;
    }

    public static final class Client
    {
        public Client(ServiceInterface service)
        {
            this.service = service;
        }
<#list methodList as method>

        public ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName} request,
                Object context) throws IOException, ServiceException
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            request.write(writer);
            final byte[] requestData = writer.toByteArray();

            final byte[] responseData = service.callMethod("${method.name}", requestData, context);

            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(responseData);
            final ${method.responseTypeFullName} response = new ${method.responseTypeFullName}(reader);
            return response;
        }

        public ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName} request)
                 throws IOException, ServiceException
        {
            return ${method.name}Method(request, null);
        }
</#list>

        private final ServiceInterface service;
    }
}
