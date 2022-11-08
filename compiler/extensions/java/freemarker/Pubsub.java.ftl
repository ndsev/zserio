<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name}
{
<#if withCodeComments>
    /**
     * Constructor from the Pub/Sub client backend.
     *
     * @param pubsub Interface for Pub/Sub client backend.
     */
</#if>
    public ${name}(zserio.runtime.pubsub.PubsubInterface pubsub)
    {
        this.pubsub = pubsub;
    }
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this Pub/Sub useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        return new zserio.runtime.typeinfo.TypeInfo.PubsubTypeInfo(
                "${schemaTypeName}",
                ${name}.class,
                <@messages_info messageList/>
        );
    }
</#if>
<#list messageList as message>
    <#if message.isPublished>

        <#if withCodeComments>
    /**
     * Publishes given message as a topic '${message.name}'.
            <#if message.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner message.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @param message Message to publish.
     */
        </#if>
    public void publish${message.name?cap_first}(${message.typeInfo.typeFullName} message)
    {
        publish${message.name?cap_first}(message, null);
    }

        <#if withCodeComments>
    /**
     * Publishes given message as a topic '${message.name}'.
            <#if message.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner message.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @param message Message to publish.
     * @param context Context specific for a particular Pub/Sub implementation.
     */
        </#if>
    public void publish${message.name?cap_first}(${message.typeInfo.typeFullName} message,
            java.lang.Object context)
    {
        publish(${message.topicDefinition}, message, context);
    }
    </#if>
    <#if message.isSubscribed>

        <#if withCodeComments>
    /**
     * Subscribes a topic '${message.name}'.
            <#if message.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner message.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @param callback Callback to be called when a message with the specified topic arrives.
     *
     * @return Subscription ID.
     */
        </#if>
    public int subscribe${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeInfo.typeFullName}> callback)
    {
        return subscribe${message.name?cap_first}(callback, null);
    }

        <#if withCodeComments>
    /**
     * Subscribes a topic '${message.name}'.
            <#if message.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner message.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @param callback Callback to be called when a message with the specified topic arrives.
     * @param context Context specific for a particular Pub/Sub implementation.
     *
     * @return Subscription ID.
     */
        </#if>
    public int subscribe${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeInfo.typeFullName}> callback,
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
        return pubsub.subscribe(${message.topicDefinition}, onRaw, context);
    }
    </#if>
</#list>
<#if hasSubscribing>

    <#if withCodeComments>
    /**
     * Unsubscribes the subscription with the given ID.
     *
     * @param subscriptionId ID of the subscription to be unsubscribed.
     */
    </#if>
    public void unsubscribe(int subscriptionId)
    {
        pubsub.unsubscribe(subscriptionId);
    }
    <#list messageList as message>
        <#if message.isSubscribed>

    private void onRaw${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeInfo.typeFullName}> callback,
            java.lang.String topic, byte[] data)
    {
        final ${message.typeInfo.typeFullName} message = zserio.runtime.io.SerializeUtil.deserializeFromBytes(
                ${message.typeInfo.typeFullName}.class, data);
        callback.invoke(topic, message);
    }
        </#if>
    </#list>
</#if>
<#if hasPublishing>

    private <MSG extends zserio.runtime.io.Writer> void publish(java.lang.String topic, MSG message,
            java.lang.Object context)
    {
        final byte[] data = zserio.runtime.io.SerializeUtil.serializeToBytes(message);
        pubsub.publish(topic, data, context);
    }
</#if>

    private final zserio.runtime.pubsub.PubsubInterface pubsub;
}
