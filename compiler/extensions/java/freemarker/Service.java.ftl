<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName/>

public final class ${name}
{
    public static abstract class Service implements zserio.runtime.service.ServiceInterface
    {
        public Service()
        {
            methodMap = new java.util.HashMap<java.lang.String, Method>();
<#list methodList as method>
            methodMap.put("${method.name}",
                new Method()
                {
                    @Override
                    public byte[] invoke(byte[] requestData, java.lang.Object context)
                            throws zserio.runtime.ZserioError
                    {
                        return ${method.name}Method(requestData, context);
                    }
                }
            );
</#list>
        }

        @Override
        public byte[] callMethod(java.lang.String methodName, byte[] requestData, java.lang.Object context)
                throws zserio.runtime.ZserioError
        {
            final Method method = methodMap.get(methodName);
            if (method == null)
            {
                throw new zserio.runtime.service.ServiceException(
                        "${serviceFullName}: Method '" + methodName + "' does not exist!");
            }
            return method.invoke(requestData, context);
        }

        public static java.lang.String serviceFullName()
        {
            return SERVICE_FULL_NAME;
        }

        public static java.lang.String[] methodNames()
        {
            return new java.lang.String[]
            {
<#list methodList as method>
                "${method.name}"<#if method?has_next>,</#if>
</#list>
            };
        }
<#list methodList as method>

        protected abstract ${method.responseTypeFullName} ${method.name}Impl(
                ${method.requestTypeFullName} request, java.lang.Object context);
</#list>
<#list methodList as method>

        private byte[] ${method.name}Method(byte[] requestData, java.lang.Object context)
                throws zserio.runtime.ZserioError
        {
            final ${method.requestTypeFullName} request =
                    zserio.runtime.io.ZserioIO.read(${method.requestTypeFullName}.class, requestData);

            final ${method.responseTypeFullName} response = ${method.name}Impl(request, context);

            final byte[] responseData = zserio.runtime.io.ZserioIO.write(response);
            return responseData;
        }
</#list>

        private interface Method
        {
            byte[] invoke(byte[] requestData, java.lang.Object context) throws zserio.runtime.ZserioError;
        }

        private static final java.lang.String SERVICE_FULL_NAME = "${serviceFullName}";

        private final java.util.Map<java.lang.String, Method> methodMap;
    }

    public static final class Client
    {
        public Client(zserio.runtime.service.ServiceInterface service)
        {
            this.service = service;
        }
<#list methodList as method>

        public ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName} request,
                java.lang.Object context) throws zserio.runtime.ZserioError
        {
            final byte[] requestData = zserio.runtime.io.ZserioIO.write(request);

            final byte[] responseData = service.callMethod("${method.name}", requestData, context);

            final ${method.responseTypeFullName} response =
                    zserio.runtime.io.ZserioIO.read(${method.responseTypeFullName}.class, responseData);
            return response;
        }

        public ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName} request)
                 throws zserio.runtime.ZserioError
        {
            return ${method.name}Method(request, null);
        }
</#list>

        private final zserio.runtime.service.ServiceInterface service;
    }
}
