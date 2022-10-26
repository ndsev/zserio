<#include "FileHeader.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

<#macro pubsub_type_name typeInfo>
    <#if typeInfo.isBytes>
        <#--
        Note that bytes is a shorthand for typing.ByteString, thus all bytes, bytearray and memoryview are
        allowed, see https://docs.python.org/3/library/typing.html#typing.ByteString.
        -->
        bytes<#t>
    <#else>
        ${typeInfo.typeFullName}<#t>
    </#if>
</#macro>
class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
    def __init__(self, pubsub: zserio.PubsubInterface) -> None:
<#if withCodeComments>
        """
        Constructor from the Pub/Sub client backend.

        :param pubsub: Interface for Pub/Sub client backend.
        """
</#if>
        self._pubsub = pubsub
<#if withTypeInfoCode>

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
    <#if withCodeComments>
        """
        Gets static information about this Pub/Sub type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        message_list: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list messageList as message>
            <@member_info_message message, message?has_next/>
    </#list>
        ]
        attribute_list = {
            zserio.typeinfo.TypeAttribute.MESSAGES : message_list
        }

        return zserio.typeinfo.TypeInfo('${schemaTypeFullName}', ${name}, attributes=attribute_list)
</#if>
<#list messageList as message>
    <#if message.isPublished>

    def publish_${message.snakeCaseName}(self, message: <@pubsub_type_name message.typeInfo/>, context: typing.Any = None) -> None:
        <#if withCodeComments>
        """
        Publishes given message as a topic '${message.name}'.

        <#if message.docComments??>
        **Description:**

        <@doc_comments_inner message.docComments, 2/>

        </#if>
        :param message: Message to publish.
        :param context: Context specific for a particular Pub/Sub implementation.
        """
        </#if>
        <#if message.typeInfo.isBytes>
        self._pubsub.publish(${message.topicDefinition}, message, context)
        <#else>
        self._publish(${message.topicDefinition}, message, context)
        </#if>
    </#if>
    <#if message.isSubscribed>

    def subscribe_${message.snakeCaseName}(self, callback: typing.Callable[[str, <@pubsub_type_name message.typeInfo/>], None], context: typing.Any = None) -> int:
        <#if withCodeComments>
        """
        Subscribes a topic '${message.name}'.

            <#if message.docComments??>
        **Description:**

        <@doc_comments_inner message.docComments, 2/>

            </#if>
        :param callback: Callback to be called when a message with the specified topic arrives.
        :param context: Context specific for a particular Pub/Sub implementation.

        :returns: Subscription ID.
        """
        </#if>
        def on_raw(topic: str, data: bytes) -> None:
        <#if message.typeInfo.isBytes>
            callback(topic, data)
        <#else>
            self._on_raw_${message.snakeCaseName}(callback, topic, data)
        </#if>
        return self._pubsub.subscribe(${message.topicDefinition}, on_raw, context)
    </#if>
</#list>
<#if hasSubscribing>

    def unsubscribe(self, subscription_id: int) -> None:
    <#if withCodeComments>
        """
        Unsubscribes the subscription with the given ID.

        :param subscription_id: ID of the subscription to be unsubscribed.
        """
    </#if>
        self._pubsub.unsubscribe(subscription_id)
    <#list messageList as message>
        <#if message.isSubscribed && !message.typeInfo.isBytes>

    def _on_raw_${message.snakeCaseName}(self, callback: typing.Callable[[str, ${message.typeInfo.typeFullName}], None], topic: str, data: bytes) -> None:
        reader = zserio.BitStreamReader(data)
        message = ${message.typeInfo.typeFullName}.from_reader(reader)
        callback(topic, message)
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

    def _publish(self, topic: str, message: typing.Any, context: typing.Any) -> None:
        writer = zserio.BitStreamWriter()
        message.write(writer)
        self._pubsub.publish(topic, writer.byte_array, context)
</#if>
