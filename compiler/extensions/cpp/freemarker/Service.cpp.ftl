<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

Service::Service() :
        m_methodMap({
<#list methodList as method>
                {"${method.name}", ::std::bind(&Service::${method.name}Method, this,
                        ::std::placeholders::_1, ::std::placeholders::_2)}<#if method?has_next>,</#if>
</#list>
        })
{
}

void Service::callMethod(const std::string& methodName, const std::vector<uint8_t>& requestData,
        std::vector<uint8_t>& responseData, void*)
{
    auto search = m_methodMap.find(methodName);
    if (search == m_methodMap.end())
        throw ::zserio::ServiceException("${serviceFullName}: Method '" + methodName + "' does not exist!");
    search->second(requestData, responseData);
}
<#list methodList as method>

void Service::${method.name}Method(const std::vector<uint8_t>& requestData, std::vector<uint8_t>& responseData)
{
    ::zserio::BitStreamReader reader(requestData.data(), requestData.size());
    const ${method.requestTypeFullName} request(reader);

    ${method.responseTypeFullName} response;
    ${method.name}Impl(request, response);

    responseData.resize((response.bitSizeOf() + 7) / 8);
    ::zserio::BitStreamWriter writer(responseData.data(), responseData.size());
    response.write(writer);
}
</#list>

const char* Service::serviceFullName() noexcept
{
    return "${serviceFullName}";
}

const ::std::array<const char*, ${methodList?size}>& Service::methodNames() noexcept
{
    static constexpr ::std::array<const char*, ${methodList?size}> names =
    {
<#list methodList as method>
        "${method.name}"<#if method?has_next>,</#if>
</#list>
    };

    return names;
}

Client::Client(::zserio::IService& service) : m_service(service)
{
}
<#list methodList as method>

void Client::${method.name}Method(${method.requestTypeFullName}& request, <#rt>
        <#lt>${method.responseTypeFullName}& response, void* context)
{
    std::vector<uint8_t> requestData((request.bitSizeOf() + 7) / 8);
    ::zserio::BitStreamWriter writer(requestData.data(), requestData.size());
    request.write(writer);

    std::vector<uint8_t> responseData;
    m_service.callMethod("${method.name}", requestData, responseData, context);

    ::zserio::BitStreamReader reader(responseData.data(), responseData.size());
    response.read(reader);
}
</#list>
<@namespace_end [name]/>
<@namespace_end package.path/>
