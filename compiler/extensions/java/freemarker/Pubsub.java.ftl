<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, packageName/>

public class ${name}
{
    public ${name}(zserio.runtime.pubsub.PubsubInterface pubsub)
    {
        this.pubsub = pubsub;
    }
<#list messageList as message>
    <#if message.isPublished>

    public void publish${message.name?cap_first}(${message.typeFullName} message)
    {
        publish${message.name?cap_first}(message, null);
    }

    public void publish${message.name?cap_first}(${message.typeFullName} message,
            java.lang.Object context)
    {
        publish("${message.topicDefinition}", message, context);
    }
    </#if>
    <#if message.isSubscribed>

    public int subscribe${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeFullName}> callback)
    {
        return subscribe${message.name?cap_first}(callback, null);
    }

    public int subscribe${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeFullName}> callback,
            java.lang.Object context)
    {
        final zserio.runtime.pubsub.PubsubInterface.Callback onRaw =
            new zserio.runtime.pubsub.PubsubInterface.Callback()
            {
                @Override
                public void invoke(java.lang.String topic, byte[] data)
                {
                    onRaw${message.name?cap_first}(callback, topic, data);
                }
            };
        return pubsub.subscribe("${message.topicDefinition}", onRaw, context);
    }
    </#if>
</#list>
<#if hasSubscribing>

    public void unsubscribe(int subscriptionId)
    {
        pubsub.unsubscribe(subscriptionId);
    }
    <#list messageList as message>
        <#if message.isSubscribed>

    private void onRaw${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeFullName}> callback,
            java.lang.String topic, byte[] data)
    {
        final ${message.typeFullName} message = zserio.runtime.io.ZserioIO.read(
                ${message.typeFullName}.class, data);
        callback.invoke(topic, message);
    }
        </#if>
    </#list>
</#if>
<#if hasPublishing>

    private <MSG extends zserio.runtime.io.Writer> void publish(java.lang.String topic, MSG message,
            java.lang.Object context)
    {
        final byte[] data = zserio.runtime.io.ZserioIO.write(message);
        pubsub.publish(topic, data, context);
    }
</#if>

    private final zserio.runtime.pubsub.PubsubInterface pubsub;
}
