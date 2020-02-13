<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.util.HashMap",
        "java.util.Map",
        "zserio.runtime.ZserioError",
        "zserio.runtime.io.ZserioIO",
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
                    public byte[] call(byte[] requestData, Object context) throws ZserioError
                    {
                        return ${method.name}Method(requestData, context);
                    }
                }
            );
</#list>
        }

        @Override
        public byte[] callMethod(String methodName, byte[] requestData, Object context) throws ZserioError
        {
            final Method method = methodMap.get(methodName);
            if (method == null)
                throw new ServiceException("${serviceFullName}: Method '" + methodName + "' does not exist!");
            return method.call(requestData, context);
        }

        public static String serviceFullName()
        {
            return SERVICE_FULL_NAME;
        }

        public static String[] methodNames()
        {
            return new String[]
            {
<#list methodList as method>
                "${method.name}"<#if method?has_next>,</#if>
</#list>
            };
        }
<#list methodList as method>

        protected abstract ${method.responseTypeFullName} ${method.name}Impl(<#rt>
                <#lt>${method.requestTypeFullName} request, Object context);
</#list>
<#list methodList as method>

        private byte[] ${method.name}Method(byte[] requestData, Object context)
                throws ZserioError
        {
            final ${method.requestTypeFullName} request =
                    ZserioIO.read(${method.requestTypeFullName}.class, requestData);

            final ${method.responseTypeFullName} response = ${method.name}Impl(request, context);

            final byte[] responseData = ZserioIO.write(response);
            return responseData;
        }
</#list>

        private interface Method
        {
            byte[] call(byte[] requestData, Object context) throws ZserioError;
        }

        private static final String SERVICE_FULL_NAME = "${serviceFullName}";

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
                Object context) throws ZserioError
        {
            final byte[] requestData = ZserioIO.write(request);

            final byte[] responseData = service.callMethod("${method.name}", requestData, context);

            final ${method.responseTypeFullName} response =
                    ZserioIO.read(${method.responseTypeFullName}.class, responseData);
            return response;
        }

        public ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName} request)
                 throws ZserioError
        {
            return ${method.name}Method(request, null);
        }
</#list>

        private final ServiceInterface service;
    }
}
