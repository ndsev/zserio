<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#macro method_name_constant_name method>
    ${method.name}_METHOD_NAME<#t>
</#macro>
<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public final class ${name}
{
<#if withTypeInfoCode>
    <#if withCodeComments>
    /**
     * Gets static information about this service useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.ServiceTypeInfo(
                "${schemaTypeName}",
                ${name}.class,
                <@methods_info methodList/>
        );
    }

</#if>
<#if withCodeComments>
    /**
     * Service part of the service {@link ${name}}.
     */
</#if>
    public static abstract class ${name}Service implements zserio.runtime.service.ServiceInterface
    {
<#if withCodeComments>
        /** Default constructor. */
</#if>
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

<#if withCodeComments>
        /**
         * Gets the service full qualified name.
         *
         * @return Service name together with its package name.
         */
</#if>
        public static java.lang.String serviceFullName()
        {
            return SERVICE_FULL_NAME;
        }

<#if withCodeComments>
        /**
         * Gets all method names of the service.
         *
         * @return Array of all method names of the service.
         */
</#if>
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

<#if withCodeComments>
    /**
     * Client part of the service {@link ${name}}.
     */
</#if>
    public static final class ${name}Client
    {
<#if withCodeComments>
        /**
         * Constructor from the service client backend.
         *
         * @param serviceClient Interface for service client backend.
         */
</#if>
        public ${name}Client(zserio.runtime.service.ServiceClientInterface serviceClient)
        {
            this.serviceClient = serviceClient;
        }
<#list methodList as method>

    <#if withCodeComments>
        /**
         * Calls method ${method.name}.
        <#if method.docComments??>
         * <p>
         * <b>Description:</b>
         * <br>
         <@doc_comments_inner method.docComments, 2/>
         *
        <#else>
         *
        </#if>
         * @param request Request to be passed to the method.
         * @param context Context specific for particular service.
         *
         * @return Response returned from the method.
         */
    </#if>
        public ${method.responseTypeInfo.typeFullName} ${method.name}Method(${method.requestTypeInfo.typeFullName} request,
                java.lang.Object context) throws zserio.runtime.ZserioError
        {
            final byte[] responseData = serviceClient.callMethod(<@method_name_constant_name method/>,
                    new zserio.runtime.service.ServiceData<>(request), context);
            final ${method.responseTypeInfo.typeFullName} response =
                    zserio.runtime.io.ZserioIO.read(${method.responseTypeInfo.typeFullName}.class, responseData);
            return response;
        }

    <#if withCodeComments>
        /**
         * Calls method ${method.name}.
        <#if method.docComments??>
         * <p>
         * <b>Description:</b>
         * <br>
         <@doc_comments_inner method.docComments, 2/>
         *
        <#else>
         *
        </#if>
         * @param request Request to be passed to the method.
         *
         * @return Response returned from the method.
         */
    </#if>
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
        <#if withCodeComments>
    /** Name definition of the service method ${method.name}. */
        </#if>
    public static final String <@method_name_constant_name method/> = "${method.name}";
    </#list>
</#if>
}
