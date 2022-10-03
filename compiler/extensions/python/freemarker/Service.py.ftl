<#include "FileHeader.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
<#if withTypeInfoCode>
    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
    <#if withCodeComments>
        """
        Gets static information about this service type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        method_list: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list methodList as method>
            <@member_info_method method method?has_next/>
    </#list>
        ]
        attribute_list = {
            zserio.typeinfo.TypeAttribute.METHODS : method_list
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeFullName}', ${name}, attributes=attribute_list)

</#if>
    class Service(zserio.ServiceInterface):
<#if withCodeComments>
        """
        Service part of the service ${name}.

    <#if docComments??>
        **Description:**

        <@doc_comments_inner docComments, 2/>
    </#if>
        """

</#if>
        def __init__(self) -> None:
<#if withCodeComments>
            """
            Default constructor.
            """

</#if>
            self._method_map = {
<#list methodList as method>
                self._METHOD_NAMES[${method?index}]: self._${method.snakeCaseName}_method<#if method?has_next>,</#if>
</#list>
            }

        def call_method(self, method_name: str, request_data: bytes, context: typing.Any = None) -> zserio.ServiceData:
<#if withCodeComments>
            """
            Calls method with the given name synchronously.

            :param method_name: Name of the service method to call.
            :param request_data: Request data to be passed to the method.
            :param context: Context specific for particular service.

            :returns: Response service data.

            :raises ServiceException: If the call fails.
            """

</#if>
            method = self._method_map.get(method_name)
            if not method:
                raise zserio.ServiceException(f"${serviceFullName}: Method '{method_name}' does not exist!")

            return method(request_data, context)

        @property
        def service_full_name(self) -> str:
<#if withCodeComments>
            """
            Gets service full name.

            :returns: Service full name.
            """

</#if>
            return self._SERVICE_FULL_NAME

        @property
        def method_names(self) -> typing.List:
<#if withCodeComments>
            """
            Gets list of service method names.

            :returns: List of service method names.
            """

</#if>
            return self._METHOD_NAMES
<#list methodList as method>

        def _${method.snakeCaseName}_impl(self, request: ${method.requestTypeInfo.typeFullName}, <#rt>
                <#lt>context: typing.Any = None) -> ${method.responseTypeInfo.typeFullName}:
            raise NotImplementedError()
</#list>
<#list methodList as method>

        def _${method.snakeCaseName}_method(self, request_data: bytes, context: typing.Any) -> zserio.ServiceData:
            reader = zserio.BitStreamReader(request_data)
            request = ${method.requestTypeInfo.typeFullName}.from_reader(reader)

            return zserio.ServiceData(self._${method.snakeCaseName}_impl(request, context))
</#list>

        _SERVICE_FULL_NAME = "${serviceFullName}"
        _METHOD_NAMES = [
<#list methodList as method>
            "${method.name}"<#if method?has_next>,</#if>
</#list>
        ]

    class Client:
<#if withCodeComments>
        """
        Client part of the service ${name}.

    <#if docComments??>
        **Description:**

        <@doc_comments_inner docComments, 2/>
    </#if>
        """

</#if>
        def __init__(self, service_client: zserio.ServiceClientInterface) -> None:
<#if withCodeComments>
            """
            Constructor from the service client backend.

            :param service_client: Interface for service client backend.
            """

</#if>
            self._service_client = service_client
<#list methodList as method>

        def ${method.clientMethodName}(self, request: ${method.requestTypeInfo.typeFullName}, <#rt>
                <#lt>context: typing.Any = None) -> ${method.responseTypeInfo.typeFullName}:
    <#if withCodeComments>
            """
            Calls method ${method.name}.

        <#if method.docComments??>
            **Description:**

            <@doc_comments_inner method.docComments, 3/>

        </#if>
            :param request: Request to be passed to the method.
            :param context: Context specific for particular service.

            :returns: Response returned from the method.
            """

    </#if>
            response_data = self._service_client.call_method("${method.name}", zserio.ServiceData(request), context)
            reader = zserio.BitStreamReader(response_data)
            response = ${method.responseTypeInfo.typeFullName}.from_reader(reader)

            return response
</#list>
