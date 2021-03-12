<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    class Service(zserio.ServiceInterface):
        def __init__(self) -> None:
            self._method_map = {
<#list methodList as method>
                "${method.name}": self._${method.snakeCaseName}_method<#if method?has_next>,</#if>
</#list>
            }

        def call_method(self, method_name: str, request_data: bytes, context: typing.Any = None) -> bytes:
            method = self._method_map.get(method_name)
            if not method:
                raise zserio.ServiceException("${serviceFullName}: Method '%s' does not exist!" % method_name)
            return method(request_data, context)
<#list methodList as method>

        def _${method.snakeCaseName}_impl(self, request: ${method.requestTypeFullName}, <#rt>
                <#lt>context: typing.Any = None) -> ${method.responseTypeFullName}:
            raise NotImplementedError()
</#list>
<#list methodList as method>

        def _${method.snakeCaseName}_method(self, request_data: bytes, context: typing.Any) -> bytes:
            reader = zserio.BitStreamReader(request_data)
            request = ${method.requestTypeFullName}.from_reader(reader)

            response = self._${method.snakeCaseName}_impl(request, context)

            writer = zserio.BitStreamWriter()
            response.write(writer)
            return writer.byte_array
</#list>

        SERVICE_FULL_NAME = "${serviceFullName}"
        METHOD_NAMES = [
<#list methodList as method>
            "${method.name}"<#if method?has_next>,</#if>
</#list>
        ]

    class Client:
        def __init__(self, service: zserio.ServiceInterface) -> None:
            self._service = service
<#list methodList as method>

        def ${method.snakeCaseName}_method(self, request: ${method.requestTypeFullName}, <#rt>
                <#lt>context: typing.Any = None) -> ${method.responseTypeFullName}:
            writer = zserio.BitStreamWriter()
            request.write(writer)
            request_data = writer.byte_array

            response_data = self._service.call_method("${method.name}", request_data, context)

            reader = zserio.BitStreamReader(response_data)
            response = ${method.responseTypeFullName}.from_reader(reader)
            return response
</#list>
