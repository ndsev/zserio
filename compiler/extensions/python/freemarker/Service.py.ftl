<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    """ Generated for ${serviceFullName} """

    class Service:
        def __init__(self):
            self._methodMap = {
<#list methodList as method>
                "${method.name}": self._${method.name}Method<#if method?has_next>,</#if>
</#list>
            }

        def callMethod(self, methodName, requestData, context=None):
            method = self._methodMap.get(methodName)
            if not method:
                raise zserio.ServiceException("${serviceFullName}: Method '%s' does not exist!" % methodName)
            return method(requestData, context)
<#list methodList as method>

        def _${method.name}Impl(self, request, context):
            raise NotImplementedError()
</#list>
<#list methodList as method>

        def _${method.name}Method(self, requestData, context):
            reader = zserio.BitStreamReader(requestData)
            request = ${method.requestTypeFullName}.fromReader(reader)

            response = self._${method.name}Impl(request, context)

            writer = zserio.BitStreamWriter()
            response.write(writer)
            return writer.getByteArray()
</#list>
        SERVICE_FULL_NAME = "${serviceFullName}"
        METHOD_NAMES = [
<#list methodList as method>
            "${method.name}"<#if method?has_next>,</#if>
</#list>
        ]

    class Client:
        def __init__(self, service):
            self._service = service
<#list methodList as method>

        def ${method.name}Method(self, request, context=None):
            writer = zserio.BitStreamWriter()
            request.write(writer)
            requestData = writer.getByteArray()

            responseData = self._service.callMethod("${method.name}", requestData, context)

            reader = zserio.BitStreamReader(responseData)
            response = ${method.responseTypeFullName}.fromReader(reader)
            return response
</#list>
