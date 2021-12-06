<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>
#include <zserio/Types.h>
#include <zserio/IService.h>
#include <zserio/AllocatorHolder.h>
#include <zserio/ServiceException.h>
<#if withTypeInfoCode>
#include <zserio/ITypeInfo.h>
</#if>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

<#if withTypeInfoCode>
const ::zserio::ITypeInfo& typeInfo();

</#if>
class Service : public ::zserio::IService, public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
    Service(const allocator_type& allocator = allocator_type());
    virtual ~Service() override = default;

    Service(const Service&) = delete;
    Service& operator=(const Service&) = delete;

    Service(Service&&) = default;
    Service& operator=(Service&&) = delete;

    virtual void callMethod(::zserio::StringView methodName, ::zserio::Span<const uint8_t> requestData,
            ::zserio::IBlobBuffer& responseData, void* context = nullptr) override;

    static ::zserio::StringView serviceFullName() noexcept;
    static const ::std::array<::zserio::StringView, ${methodList?size}>& methodNames() noexcept;

private:
<#if methodList?has_content>
<#list methodList as method>
    virtual ${method.responseTypeFullName} ${method.name}Impl(const ${method.requestTypeFullName}& request<#rt>
            <#lt>, void* context) = 0;
</#list>

<#list methodList as method>
    void ${method.name}Method(::zserio::Span<const uint8_t> requestData, ::zserio::IBlobBuffer& responseData,
            void* context);
</#list>
</#if>
};

class Client : public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
    explicit Client(::zserio::IService& service, const allocator_type& allocator = allocator_type());
    ~Client() = default;

    Client(const Client&) = delete;
    Client& operator=(const Client&) = delete;

    Client(Client&&) = default;
    Client& operator=(Client&&) = delete;
<#list methodList as method>

    ${method.responseTypeFullName} ${method.name}Method(${method.requestTypeFullName}& request, <#rt>
            <#lt>void* context = nullptr);
</#list>

private:
    ::zserio::IService& m_service;
};
<@namespace_end [name]/>
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
