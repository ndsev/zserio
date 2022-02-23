<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
<#else>
<@type_includes types.bitBuffer/>
</#if>
<@type_includes types.vector/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

<#if withTypeInfoCode>
const ::zserio::ITypeInfo& typeInfo()
{
    static const <@info_array_type "::zserio::MethodInfo", methodList?size/> methods<#rt>
    <#if methodList?has_content>
        <#lt> = {
        <#list methodList as method>
        <@method_info method method?has_next/>
        </#list>
    };
    <#else>
        <#lt>;
    </#if>

    static const ::zserio::ServiceTypeInfo typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"), methods
    };

    return typeInfo;
}

</#if>
Service::Service(const allocator_type& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator)
{}

${types.serviceDataPtr.name} Service::callMethod(
        ::zserio::StringView methodName, ::zserio::Span<const uint8_t> requestData, void* context)
{
<#list methodList as method>
    <#if !method?is_first>else </#if>if (methodName == methodNames()[${method?index}])
        return ${method.name}Method(requestData, context);
</#list>
    else
        throw ::zserio::ServiceException("${serviceFullName}: Method '") + methodName + "' does not exist!";
}

::zserio::StringView Service::serviceFullName() noexcept
{
    static const ::zserio::StringView serviceFullName = ::zserio::makeStringView("${serviceFullName}");
    return serviceFullName;
}

const ::std::array<::zserio::StringView, ${methodList?size}>& Service::methodNames() noexcept
{
    static constexpr ::std::array<::zserio::StringView, ${methodList?size}> names =
    {
<#list methodList as method>
        ::zserio::makeStringView("${method.name}")<#if method?has_next>,</#if>
</#list>
    };

    return names;
}
<#list methodList as method>

${types.serviceDataPtr.name} Service::${method.name}Method(
        ::zserio::Span<const uint8_t> requestData, void* context)
{
    ::zserio::BitStreamReader reader(requestData.data(), requestData.size());
    const ${method.requestTypeInfo.typeFullName} request(reader, get_allocator_ref());

    class ResponseData : public ${types.serviceDataPtr.name}::element_type
    {
    public:
        ResponseData(${method.responseTypeInfo.typeFullName}&& response, const allocator_type& allocator) :
    <#if withReflectionCode>
                m_response(std::move(response)), m_serviceData(m_response.reflectable(allocator), allocator)
    <#else>
                m_response(std::move(response)), m_serviceData(m_response, allocator)
    </#if>
        {}

        virtual ${types.reflectablePtr.name} getReflectable() const override
        {
            return m_serviceData.getReflectable();
        }

        virtual ::zserio::Span<const uint8_t> getData() const override
        {
            return m_serviceData.getData();
        }

    private:
        ${method.responseTypeInfo.typeFullName} m_response;
        ${types.serviceData.name} m_serviceData;
    };

    return ::std::allocate_shared<ResponseData>(get_allocator(),
            ${method.name}Impl(request, context), get_allocator());
}
</#list>

Client::Client(${types.serviceClient.name}& service, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator),
        m_service(service)
{
}
<#list methodList as method>

${method.responseTypeInfo.typeFullName} Client::${method.name}Method(${method.requestTypeInfo.typeFullName}& request, <#rt>
        <#lt>void* context)
{
    <#if withReflectionCode>
    const ${types.serviceData.name} requestData(request.reflectable(get_allocator_ref()), get_allocator_ref());
    <#else>
    const ${types.serviceData.name} requestData(request, get_allocator_ref());
    </#if>

    auto responseData = m_service.callMethod(::zserio::makeStringView("${method.name}"), requestData, context);

    ::zserio::BitStreamReader reader(responseData.data(), responseData.size());
    return ${method.responseTypeInfo.typeFullName}(reader, get_allocator_ref());
}
</#list>
<@namespace_end [name]/>
<@namespace_end package.path/>
