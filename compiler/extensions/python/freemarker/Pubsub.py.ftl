<#include "FileHeader.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
    def __init__(self, pubsub):
        self._pubsub = pubsub
<#if hasSubscriptions>

    <#list messageList as message>
        <#if message.isSubscribed>
        self._subscribers${message.name?cap_first} = {}
        </#if>
    </#list>
</#if>
<#list messageList as message>
    <#if message.isPublished>

    def publish${message.name?cap_first}(self, message, context=None):
        self._publish("${message.topicDefinition}", message, context)
    </#if>
    <#if message.isSubscribed>

    def subscribe${message.name?cap_first}(self, callback, context=None):
        subscriptionId = self._pubsub.reserveId()
        if subscriptionId in self._subscribers${message.name?cap_first}:
            raise zserio.PubsubException("Pubsub ${name}: Subscription ID '%d' already in use!" %
                                         subscriptionId)
        self._subscribers${message.name?cap_first}[subscriptionId] = callback
        self._pubsub.subscribe(subscriptionId, "${message.topicDefinition}",
                               self._onRaw${message.name?cap_first}, context)
        return subscriptionId
    </#if>
</#list>
<#if hasSubscriptions>

    def unsubscribe(self, subscriptionId):
    <#list messageList as message>
        <#if message.isSubscribed>
        if subscriptionId in self._subscribers${message.name?cap_first}:
            self._pubsub.unsubscribe(subscriptionId)
            self._subscribers${message.name?cap_first}.pop(subscriptionId)
            return
        </#if>
    </#list>

        raise zserio.PubsubException("Pubsub ${name}: Subscription ID '%d' was not subscribed!" %
                                     subscriptionId)
    <#list messageList as message>
        <#if message.isSubscribed>

    def _onRaw${message.name?cap_first}(self, subscriptionId, topic, data):
        reader = zserio.BitStreamReader(data)
        message = ${message.typeFullName}.fromReader(reader)

        if not subscriptionId in self._subscribers${message.name?cap_first}:
            raise zserio.PubsubException(("Pubsub ${name}: Unknown subscription ID '%d' for '${message.name}'" +
                                          "message!") % subscriptionId)
        self._subscribers${message.name?cap_first}[subscriptionId](topic, message)
        </#if>
    </#list>
</#if>
<#if hasPublifications>

    def _publish(self, topic, message, context):
        writer = zserio.BitStreamWriter()
        message.write(writer)
        self._pubsub.publish(topic, writer.getByteArray(), context)
</#if>
