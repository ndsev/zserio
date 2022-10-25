<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>
#include <zserio/Types.h>
<@type_includes types.service/>
#include <zserio/AllocatorHolder.h>
#include <zserio/ServiceException.h>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
</#if>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

<#if withTypeInfoCode>
    <#if withCodeComments>
/**
 * Gets static information about this service useful for generic introspection.
 *
 * \return Const reference to Zserio type information.
 */
    </#if>
const ${types.typeInfo.name}& typeInfo();

</#if>
<#if withCodeComments>
/**
 * Service part of the service ${name}.
 *
    <#if docComments??>
 * \b Description
 *
 <@doc_comments_inner docComments/>
    </#if>
 */
</#if>
class Service :
        public ${types.service.name},
        public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if withCodeComments>
    /**
     * Default constructor.
     *
     * \param allocator Allocator to construct from.
     */
</#if>
    Service(const allocator_type& allocator = allocator_type());
<#if withCodeComments>

    /** Default destructor. */
</#if>
    virtual ~Service() override = default;

<#if withCodeComments>
    /** Disables copy constructor. */
</#if>
    Service(const Service&) = delete;
<#if withCodeComments>
    /** Disables assignment operator. */
</#if>
    Service& operator=(const Service&) = delete;

<#if withCodeComments>
    /** Default move constructor. */
</#if>
    Service(Service&&) = default;
<#if withCodeComments>
    /** Disables move assignment operator. */
</#if>
    Service& operator=(Service&&) = delete;

<#if withCodeComments>
    /**
     * Calls method with the given name synchronously.
     *
     * \param methodName Name of the service method to call.
     * \param requestData Request data to be passed to the method.
     * \param context Context specific for particular service.
     *
     * \return Created response data.
     *
     * \throw ServiceException if the call fails.
     */
</#if>
    virtual ${types.serviceDataPtr.name} callMethod(
            ::zserio::StringView methodName, ::zserio::Span<const uint8_t> requestData,
            void* context = nullptr) override;

<#if withCodeComments>
    /**
     * Gets the service full qualified name.
     *
     * \return Service name together with its package name.
     */
</#if>
    static ::zserio::StringView serviceFullName() noexcept;
<#if withCodeComments>

    /**
     * Gets all method names of the service.
     *
     * \return Array of all method names of the service.
     */
</#if>
    static const ::std::array<::zserio::StringView, ${methodList?size}>& methodNames() noexcept;

private:
<#if methodList?has_content>
<#list methodList as method>
    virtual ${method.responseTypeInfo.typeFullName} ${method.name}Impl(const ${method.requestTypeInfo.typeFullName}& request<#rt>
            <#lt>, void* context) = 0;
</#list>

<#list methodList as method>
    ${types.serviceDataPtr.name} ${method.name}Method(
            ::zserio::Span<const uint8_t> requestData, void* context);
</#list>
</#if>
};

<#if withCodeComments>
/**
 * Client part of the service ${name}.
 *
    <#if docComments??>
 * \b Description
 *
 <@doc_comments_inner docComments/>
    </#if>
 */
</#if>
class Client : public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if withCodeComments>
    /**
     * Constructor from the service client backend.
     *
     * \param service Interface for service client backend.
     * \param allocator Allocator to construct from.
     */
</#if>
    explicit Client(${types.serviceClient.name}& service, const allocator_type& allocator = allocator_type());
<#if withCodeComments>

    /** Default destructor. */
</#if>
    ~Client() = default;

<#if withCodeComments>
    /** Disables copy constructor. */
</#if>
    Client(const Client&) = delete;
<#if withCodeComments>
    /** Disables assignment operator. */
</#if>
    Client& operator=(const Client&) = delete;

<#if withCodeComments>
    /** Default move constructor. */
</#if>
    Client(Client&&) = default;
<#if withCodeComments>
    /** Disables move assignment operator. */
</#if>
    Client& operator=(Client&&) = delete;
<#list methodList as method>

<#if withCodeComments>
    /**
     * Calls method ${method.name}.
     *
    <#if method.docComments??>
     * \b Description
     *
     <@doc_comments_inner method.docComments, 1/>
     *
    </#if>
     * \param request Request to be passed to the method.
     * \param context Context specific for particular service.
     *
     * \return Response returned from the method.
     */
</#if>
    ${method.responseTypeInfo.typeFullName} ${method.name}Method(${method.requestTypeInfo.typeFullName}& request, <#rt>
            <#lt>void* context = nullptr);
</#list>

private:
    ${types.serviceClient.name}& m_service;
};
<@namespace_end [name]/>
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
