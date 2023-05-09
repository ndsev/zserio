<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "Service.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
<#else>
<@type_includes types.bitBuffer/>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

<#if withTypeInfoCode>
const ${types.typeInfo.name}& typeInfo()
{
    using allocator_type = ${types.allocator.default};

    static const <@info_array_type "::zserio::BasicMethodInfo<allocator_type>", methodList?size/> methods<#rt>
    <#if methodList?has_content>
        <#lt> = {
        <#list methodList as method>
        <@method_info method method?has_next/>
        </#list>
    };
    <#else>
        <#lt>;
    </#if>

    static const ::zserio::ServiceTypeInfo<${types.allocator.default}> typeInfo = {
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
    if (methodName == methodNames()[${method?index}])
        return ${method.name}Method(requestData, context);
</#list>
    throw ::zserio::ServiceException("${serviceFullName}: Method '") << methodName << "' does not exist!";
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
    <#if !method.requestTypeInfo.isBytes>
    ::zserio::BitStreamReader reader(requestData.data(), requestData.size());
    const ${method.requestTypeInfo.typeFullName} request(reader, get_allocator_ref());

    </#if>
    <#if method.responseTypeInfo.isBytes>
    return ::std::allocate_shared<${types.rawServiceDataHolder.name}>(get_allocator_ref(),
            ${method.name}Impl(request<#if method.requestTypeInfo.isBytes>Data</#if>, context));
    <#else>
    class ResponseData : public ${types.serviceDataPtr.name}::element_type
    {
    public:
        ResponseData(${method.responseTypeInfo.typeFullName}&& response, const allocator_type& allocator) :
        <#if withReflectionCode>
                m_response(std::move(response)), m_serviceData(m_response.reflectable(allocator), allocator)
        <#else>
                m_serviceData(response, allocator)
        </#if>
        {}

        ${types.reflectableConstPtr.name} getReflectable() const override
        {
            return m_serviceData.getReflectable();
        }

        ::zserio::Span<const uint8_t> getData() const override
        {
            return m_serviceData.getData();
        }

    private:
        <#if withReflectionCode>
        ${method.responseTypeInfo.typeFullName} m_response;
        ${types.reflectableServiceData.name} m_serviceData;
        <#else>
        ${types.objectServiceData.name} m_serviceData;
        </#if>
    };

    return ::std::allocate_shared<ResponseData>(get_allocator_ref(),
            ${method.name}Impl(request<#if method.requestTypeInfo.isBytes>Data</#if>, context), <#rt>
            <#lt>get_allocator_ref());
    </#if>
}
</#list>

Client::Client(${types.serviceClient.name}& service, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator),
        m_service(service)
{
}
<#list methodList as method>

${method.responseTypeInfo.typeFullName} Client::${method.name}Method(<#rt>
        <#lt><@service_arg_type_name method.requestTypeInfo/> request, void* context)
{
    <#if method.requestTypeInfo.isBytes>
    const ${types.rawServiceDataView.name} requestData(request);
    <#else>
        <#if withReflectionCode>
    const ${types.reflectableServiceData.name} requestData(request.reflectable(get_allocator_ref()), get_allocator_ref());
        <#else>
    const ${types.objectServiceData.name} requestData(request, get_allocator_ref());
        </#if>
    </#if>

    auto responseData = m_service.callMethod(::zserio::makeStringView("${method.name}"), requestData, context);
    <#if method.responseTypeInfo.isBytes>
    return responseData;
    <#else>

    ::zserio::BitStreamReader reader(responseData.data(), responseData.size());
    return ${method.responseTypeInfo.typeFullName}(reader, get_allocator_ref());
    </#if>
}
</#list>
<@namespace_end [name]/>
<@namespace_end package.path/>
