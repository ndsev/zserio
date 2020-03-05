<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self, pubsub):
        self._pubsub = pubsub
<#list messageList as message>
    <#if message.isPublished>

    def publish${message.name?cap_first}(self, message, context=None):
        self._publish("${message.topicDefinition}", message, context)
    </#if>
    <#if message.isSubscribed>

    def subscribe${message.name?cap_first}(self, callback, context=None):
        def onRaw(topic, data):
            self._onRaw${message.name?cap_first}(callback, topic, data)
        return self._pubsub.subscribe("${message.topicDefinition}", onRaw, context)
    </#if>
</#list>
<#if hasSubscribing>

    def unsubscribe(self, subscriptionId):
        self._pubsub.unsubscribe(subscriptionId)
    <#list messageList as message>
        <#if message.isSubscribed>

    def _onRaw${message.name?cap_first}(self, callback, topic, data):
        reader = zserio.BitStreamReader(data)
        message = ${message.typeFullName}.fromReader(reader)
        callback(topic, message)
        </#if>
    </#list>
</#if>
<#if hasPublishing>

    def _publish(self, topic, message, context):
        writer = zserio.BitStreamWriter()
        message.write(writer)
        self._pubsub.publish(topic, writer.getByteArray(), context)
</#if>
