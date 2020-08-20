<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self, pubsub: zserio.PubsubInterface) -> None:
        self._pubsub = pubsub
<#list messageList as message>
    <#if message.isPublished>

    def publish${message.name?cap_first}(self, message: ${message.typeFullName}, context: typing.Any = None) -> None:
        self._publish(${message.topicDefinition}, message, context)
    </#if>
    <#if message.isSubscribed>

    def subscribe${message.name?cap_first}(self, callback: typing.Callable[[str, ${message.typeFullName}], None], context: typing.Any = None) -> int:
        def onRaw(topic: str, data: bytes) -> None:
            self._onRaw${message.name?cap_first}(callback, topic, data)
        return self._pubsub.subscribe(${message.topicDefinition}, onRaw, context)
    </#if>
</#list>
<#if hasSubscribing>

    def unsubscribe(self, subscriptionId: int) -> None:
        self._pubsub.unsubscribe(subscriptionId)
    <#list messageList as message>
        <#if message.isSubscribed>

    def _onRaw${message.name?cap_first}(self, callback: typing.Callable[[str, ${message.typeFullName}], None], topic: str, data: bytes) -> None:
        reader = zserio.BitStreamReader(data)
        message = ${message.typeFullName}.fromReader(reader)
        callback(topic, message)
        </#if>
    </#list>
</#if>
<#if hasPublishing>

    def _publish(self, topic: str, message: typing.Any, context: typing.Any) -> None:
        writer = zserio.BitStreamWriter()
        message.write(writer)
        self._pubsub.publish(topic, writer.getByteArray(), context)
</#if>
