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
        <#if message.typeInfo.isBytes>
        pubsub.publish(${message.topicDefinition}, message, context);
        <#else>
        publish(${message.topicDefinition}, message, context);
        </#if>
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
        <#if message.typeInfo.isBytes>
                    callback.invoke(topic, data);
        <#else>
                    onRaw${message.name?cap_first}(callback, topic, data);
        </#if>
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
        <#if message.isSubscribed && !message.typeInfo.isBytes>

    private void onRaw${message.name?cap_first}(
            zserio.runtime.pubsub.PubsubCallback<${message.typeInfo.typeFullName}> callback,
            java.lang.String topic, byte[] data)
    {
        try
        {
            final zserio.runtime.io.ByteArrayBitStreamReader reader =
                    new zserio.runtime.io.ByteArrayBitStreamReader(data);
            final ${message.typeInfo.typeFullName} message = new ${message.typeInfo.typeFullName}(reader);
            callback.invoke(topic, message);
        }
        catch (java.io.IOException exception)
        {
            throw new zserio.runtime.ZserioError("${name}: " + exception, exception);
        }
    }
        </#if>
    </#list>
</#if>
<#function has_published_object messageList>
    <#list messageList as message>
        <#if message.isPublished && !message.typeInfo.isBytes>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
<#if hasPublishing && has_published_object(messageList)>

    private <ZSERIO_MESSAGE extends zserio.runtime.io.Writer> void publish(
            java.lang.String topic, ZSERIO_MESSAGE message, java.lang.Object context)
    {
        try
        {
            final zserio.runtime.io.ByteArrayBitStreamWriter writer =
                    new zserio.runtime.io.ByteArrayBitStreamWriter();
            message.write(writer);
            final byte[] data = writer.toByteArray();
            pubsub.publish(topic, data, context);
        }
        catch (java.io.IOException exception)
        {
            throw new zserio.runtime.ZserioError("${name}: " + exception, exception);
        }
    }
</#if>

    private final zserio.runtime.pubsub.PubsubInterface pubsub;
}
