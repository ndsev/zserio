<#include "FileHeader.inc.ftl"/>
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self, pubsub: zserio.PubsubInterface) -> None:
        self._pubsub = pubsub
<#if withTypeInfoCode>

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
        messages: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list messageList as message>
            <@member_info_message message, message?has_next/>
    </#list>
        ]
        attributes = {
            zserio.typeinfo.TypeAttribute.MESSAGES : messages
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeName}', ${name}, attributes=attributes)
</#if>
<#list messageList as message>
    <#if message.isPublished>

    def publish_${message.snakeCaseName}(self, message: ${message.typeFullName}, context: typing.Any = None) -> None:
        self._publish(${message.topicDefinition}, message, context)
    </#if>
    <#if message.isSubscribed>

    def subscribe_${message.snakeCaseName}(self, callback: typing.Callable[[str, ${message.typeFullName}], None], context: typing.Any = None) -> int:
        def on_raw(topic: str, data: bytes) -> None:
            self._on_raw_${message.snakeCaseName}(callback, topic, data)
        return self._pubsub.subscribe(${message.topicDefinition}, on_raw, context)
    </#if>
</#list>
<#if hasSubscribing>

    def unsubscribe(self, subscription_id: int) -> None:
        self._pubsub.unsubscribe(subscription_id)
    <#list messageList as message>
        <#if message.isSubscribed>

    def _on_raw_${message.snakeCaseName}(self, callback: typing.Callable[[str, ${message.typeFullName}], None], topic: str, data: bytes) -> None:
        reader = zserio.BitStreamReader(data)
        message = ${message.typeFullName}.from_reader(reader)
        callback(topic, message)
        </#if>
    </#list>
</#if>
<#if hasPublishing>

    def _publish(self, topic: str, message: typing.Any, context: typing.Any) -> None:
        writer = zserio.BitStreamWriter()
        message.write(writer)
        self._pubsub.publish(topic, writer.byte_array, context)
</#if>
