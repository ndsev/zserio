<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>

<#assign packagePrefix>
    <#if packageName?has_content>${packageName}.</#if><#t>
</#assign>
class ${name}Stub():
    def __init__(self, channel):
<#if rpcList?has_content>
    <#list rpcList as rpc>
        def ${rpc.name}_request_serializer(request):
            writer = zserio.BitStreamWriter()
            request.write(writer)
            return bytes(writer.getByteArray())

        def ${rpc.name}_response_deserializer(response):
            reader = zserio.BitStreamReader(response)
            return ${rpc.responseTypeFullName}.fromReader(reader)

        self.${rpc.name} = channel.${rpc.requestStreaming}_${rpc.responseStreaming}(
            '/${packagePrefix}${name}/${rpc.name}',
            request_serializer=${rpc.name}_request_serializer,
            response_deserializer=${rpc.name}_response_deserializer
        )
        <#if rpc?has_next>

        </#if>
    </#list>
<#else>
        pass
</#if>

class ${name}Servicer():
<#if rpcList?has_content>
    <#list rpcList as rpc>
    def ${rpc.name}(self, _request, context):
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details("Method not implemented!")
        raise NotImplementedError('Method not implemented!')
        <#if rpc?has_next>

        </#if>
    </#list>
<#else>
    pass
</#if>

def add_${name}Servicer_to_server(servicer, server):
<#list rpcList as rpc>
    def ${rpc.name}_request_deserializer(request):
        reader = zserio.BitStreamReader(request)
        return ${rpc.requestTypeFullName}.fromReader(reader)

    def ${rpc.name}_response_serializer(response):
        writer = zserio.BitStreamWriter()
        response.write(writer)
        return bytes(writer.getByteArray())

</#list>
    rpc_method_handlers = {
<#list rpcList as rpc>
        '${rpc.name}': grpc.${rpc.requestStreaming}_${rpc.responseStreaming}_rpc_method_handler(
            servicer.${rpc.name},
            request_deserializer=${rpc.name}_request_deserializer,
            response_serializer=${rpc.name}_response_serializer
        )<#if rpc?has_next>,</#if>
</#list>
    }

    generic_handler = grpc.method_handlers_generic_handler('${packagePrefix}${name}', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))
