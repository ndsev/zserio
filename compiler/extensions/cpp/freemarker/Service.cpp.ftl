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

${types.responseDataPtr.name} Service::callMethod(
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

${types.responseDataPtr.name} Service::${method.name}Method(
        ::zserio::Span<const uint8_t> requestData, void* context)
{
    ::zserio::BitStreamReader reader(requestData.data(), requestData.size());
    const ${method.requestTypeInfo.typeFullName} request(reader, get_allocator_ref());

    class ResponseData : public ::zserio::IBasicResponseData<allocator_type>
    {
    public:
    <#if withReflectionCode>
        ResponseData(${method.responseTypeInfo.typeFullName}&& response, const allocator_type& allocator) :
                m_response(std::move(response)), m_reflectable(m_response.reflectable(allocator)),
                m_data(allocator)
        {}
    <#else>
        ResponseData(${method.responseTypeInfo.typeFullName}&& response, const allocator_type& allocator) :
                m_data(response.bitSizeOf(), allocator)
        {
            ::zserio::BitStreamWriter writer(m_data);
            response.write(writer);
        }
    </#if>

        virtual ${types.reflectablePtr.name} getReflectable() override
        {
    <#if withReflectionCode>
            return m_reflectable;
    <#else>
            return nullptr;
    </#if>
        }

        virtual ::zserio::Span<const uint8_t> getData() const override
        {
    <#if withReflectionCode>
            if (m_data.getBitSize() == 0)
            {
                // lazy initialization
                m_data = ${types.bitBuffer.name}(m_reflectable->bitSizeOf(), m_data.get_allocator());
                ::zserio::BitStreamWriter writer(m_data);
                m_reflectable->write(writer);
            }
    </#if>
            return ::zserio::Span<const uint8_t>(m_data.getBuffer(), m_data.getByteSize());
        }

    private:
    <#if withReflectionCode>
        ${method.responseTypeInfo.typeFullName} m_response;
        ${types.reflectablePtr.name} m_reflectable;
        mutable ${types.bitBuffer.name} m_data;
    <#else>
        ${types.bitBuffer.name} m_data;
    </#if>
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
    const ${types.requestData.name} requestData(request.reflectable(get_allocator_ref()), get_allocator_ref());
    <#else>
    const ${types.requestData.name} requestData(request, get_allocator_ref());
    </#if>

    auto responseData = m_service.callMethod(::zserio::makeStringView("${method.name}"), requestData, context);

    ::zserio::BitStreamReader reader(responseData.data(), responseData.size());
    return ${method.responseTypeInfo.typeFullName}(reader, get_allocator_ref());
}
</#list>
<@namespace_end [name]/>
<@namespace_end package.path/>
