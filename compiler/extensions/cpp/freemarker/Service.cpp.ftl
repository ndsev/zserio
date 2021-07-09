<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
<@type_includes types.vector/>
<@type_includes types.blobBuffer/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

Service::Service(const allocator_type& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator)
{}

void Service::callMethod(::zserio::StringView methodName, ::zserio::Span<const uint8_t> requestData,
        ::zserio::IBlobBuffer& responseData, void* context)
{
<#list methodList as method>
    <#if !method?is_first>else </#if>if (methodName == methodNames()[${method?index}])
        ${method.name}Method(requestData, responseData, context);
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

void Service::${method.name}Method(::zserio::Span<const uint8_t> requestData,
        ::zserio::IBlobBuffer& responseData, void* context)
{
    ::zserio::BitStreamReader reader(requestData.data(), requestData.size());
    const ${method.requestTypeFullName} request(reader, get_allocator_ref());

    ${method.responseTypeFullName} response = ${method.name}Impl(request, context);

    responseData.resize((response.bitSizeOf() + 7) / 8);
    ::zserio::BitStreamWriter writer(responseData.data());
    response.write(writer);
}
</#list>

Client::Client(::zserio::IService& service, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator),
        m_service(service)
{
}
<#list methodList as method>

${method.responseTypeFullName} Client::${method.name}Method(${method.requestTypeFullName}& request, <#rt>
        <#lt>void* context)
{
    <@vector_type_name "uint8_t"/> requestData((request.bitSizeOf() + 7) / 8, 0, get_allocator_ref());
    ::zserio::BitStreamWriter writer(requestData.data(), requestData.size());
    request.write(writer);

    ${types.blobBuffer.name} responseData(get_allocator_ref());
    m_service.callMethod(::zserio::makeStringView("${method.name}"), requestData, responseData, context);

    ::zserio::BitStreamReader reader(responseData.data());
    return ${method.responseTypeFullName}(reader, get_allocator_ref());
}
</#list>
<@namespace_end [name]/>
<@namespace_end package.path/>
