<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <memory>
#include <zserio/AllocatorHolder.h>
#include <zserio/IPubsub.h>
#include <zserio/PubsubException.h>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
</#if>
<@type_includes types.vector/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name} : public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if withCodeComments>
    /**
     * Constructor from the Pub/Sub client backend.
     *
     * \param pubsub Interface for Pub/Sub client backend.
     * \param allocator Allocator to construct from.
     */
</#if>
    explicit ${name}(::zserio::IPubsub& pubsub, const allocator_type& allocator = allocator_type());
<#if withCodeComments>

    /** Default destructor. */
</#if>
    ~${name}() = default;

<#if withCodeComments>
    /** Disables copy constructor. */
</#if>
    ${name}(const ${name}&) = delete;
<#if withCodeComments>
    /** Disables assignment operator. */
</#if>
    ${name}& operator=(const ${name}&) = delete;

<#if withCodeComments>
    /** Default move constructor. */
</#if>
    ${name}(${name}&&) = default;
<#if withCodeComments>
    /** Disables move assignment operator. */
</#if>
    ${name}& operator=(${name}&&) = delete;
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this Pub/Sub useful for generic introspection.
     *
     * \return Const reference to Zserio type information.
     */
    </#if>
    static const ${types.typeInfo.name}& typeInfo();
</#if>
<#if hasSubscribing>

    <#if withCodeComments>
    /**
     * Interface for callback used for Pub/Sub subscription.
     */
    </#if>
    template <typename ZSERIO_MESSAGE>
    class ${name}Callback
    {
    public:
    <#if withCodeComments>
        /** Default destructor. */
    </#if>
        virtual ~${name}Callback() = default;
    <#if withCodeComments>

        /**
         * Function call operator overload.
         *
         * \param topic Topic definition to be used for Pub/Sub subcription.
         * \param message Message to be used for Pub/Sub subcription.
         */
    </#if>
        virtual void operator()(::zserio::StringView topic, const ZSERIO_MESSAGE& message) = 0;
    };
</#if>
<#list messageList as message>
    <#if message.isPublished>

        <#if withCodeComments>
    /**
     * Publishes given message as a topic '${message.name}'.
     *
            <#if message.docComments??>
     * \b Description
     *
     <@doc_comments_inner message.docComments, 1/>
     *
            </#if>
     * \param message Message to publish.
     * \param context Context specific for a particular Pub/Sub implementation.
     */
        </#if>
    void publish${message.name?cap_first}(${message.typeInfo.typeFullName}& message, void* context = nullptr);
    </#if>
    <#if message.isSubscribed>

        <#if withCodeComments>
    /**
     * Subscribes a topic '${message.name}'.
     *
            <#if message.docComments??>
     * \b Description
     *
     <@doc_comments_inner message.docComments, 1/>
     *
            </#if>
     * \param callback Callback to be called when a message with the specified topic arrives.
     * \param context Context specific for a particular Pub/Sub implementation.
     *
     * \return Subscription ID.
     * \throw PubsubException when subscribing fails.
     */
        </#if>
    ::zserio::IPubsub::SubscriptionId subscribe${message.name?cap_first}(
            const ::std::shared_ptr<${name}Callback<${message.typeInfo.typeFullName}>>& callback,
            void* context = nullptr);
    </#if>
</#list>
<#if hasSubscribing>

    <#if withCodeComments>
    /**
     * Unsubscribes the subscription with the given ID.
     *
     * \param id ID of the subscription to be unsubscribed.
     *
     * \throw PubsubException when unsubscribing fails.
     */
    </#if>
    void unsubscribe(::zserio::IPubsub::SubscriptionId id);
</#if>

private:
<#if hasPublishing>
    template <typename ZSERIO_MESSAGE>
    void publish(ZSERIO_MESSAGE& message, ::zserio::StringView topic, void* context);

</#if>
    ::zserio::IPubsub& m_pubsub;
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
