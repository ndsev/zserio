<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#macro method_name_constant_name method>
    ${method.name}_METHOD_NAME<#t>
</#macro>
public final class ${name}
{
<#if withTypeInfoCode>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.ServiceTypeInfo(
                "${schemaTypeName}",
                <@methods_info methodList/>
        );
    }

</#if>
    public static abstract class ${name}Service implements zserio.runtime.service.ServiceInterface
    {
        public ${name}Service()
        {
            methodMap = new java.util.HashMap<java.lang.String, Method>();
<#list methodList as method>
            methodMap.put(<@method_name_constant_name method/>,
                new Method()
                {
                    @Override
                    public <T extends zserio.runtime.io.Writer> zserio.runtime.service.ServiceData<T> invoke(
                            byte[] requestData, java.lang.Object context) throws zserio.runtime.ZserioError
                    {
                        return ${method.name}Method(requestData, context);
                    }
                }
            );
</#list>
        }

        @Override
        public <T extends zserio.runtime.io.Writer> zserio.runtime.service.ServiceData<T> callMethod(
                java.lang.String methodName, byte[] requestData, java.lang.Object context)
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
                <@method_name_constant_name method/><#if method?has_next>,</#if>
</#list>
            };
        }
<#list methodList as method>

        protected abstract ${method.responseTypeInfo.typeFullName} ${method.name}Impl(
                ${method.requestTypeInfo.typeFullName} request, java.lang.Object context);
</#list>
<#list methodList as method>

        @SuppressWarnings("unchecked")
        private <T extends zserio.runtime.io.Writer> zserio.runtime.service.ServiceData<T> ${method.name}Method(
                byte[] requestData, java.lang.Object context) throws zserio.runtime.ZserioError
        {
            final ${method.requestTypeInfo.typeFullName} request =
                    zserio.runtime.io.ZserioIO.read(${method.requestTypeInfo.typeFullName}.class, requestData);
            final ${method.responseTypeInfo.typeFullName} response = ${method.name}Impl(request, context);

            return (zserio.runtime.service.ServiceData<T>)new zserio.runtime.service.ServiceData<${method.responseTypeInfo.typeFullName}>(response);
        }
</#list>

        private interface Method
        {
            public <T extends zserio.runtime.io.Writer> zserio.runtime.service.ServiceData<T> invoke(
                    byte[] requestData, java.lang.Object context) throws zserio.runtime.ZserioError;
        }

        private static final java.lang.String SERVICE_FULL_NAME = "${serviceFullName}";

        private final java.util.Map<java.lang.String, Method> methodMap;
    }

    public static final class ${name}Client
    {
        public ${name}Client(zserio.runtime.service.ServiceClientInterface serviceClient)
        {
            this.serviceClient = serviceClient;
        }
<#list methodList as method>

        public ${method.responseTypeInfo.typeFullName} ${method.name}Method(${method.requestTypeInfo.typeFullName} request,
                java.lang.Object context) throws zserio.runtime.ZserioError
        {
            final byte[] responseData = serviceClient.callMethod(<@method_name_constant_name method/>,
                    new zserio.runtime.service.ServiceData<>(request), context);
            final ${method.responseTypeInfo.typeFullName} response =
                    zserio.runtime.io.ZserioIO.read(${method.responseTypeInfo.typeFullName}.class, responseData);
            return response;
        }

        public ${method.responseTypeInfo.typeFullName} ${method.name}Method(${method.requestTypeInfo.typeFullName} request)
                 throws zserio.runtime.ZserioError
        {
            return ${method.name}Method(request, null);
        }
</#list>

        private final zserio.runtime.service.ServiceClientInterface serviceClient;
    }
<#if methodList?has_content>

    <#list methodList as method>
    public static final String <@method_name_constant_name method/> = "${method.name}";
    </#list>
</#if>
}
