<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>
#include <string>
#include <vector>
#include <map>
#include <functional>
#include "zserio/Types.h"
#include "zserio/IService.h"
#include "zserio/ServiceException.h"
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>
<@namespace_begin [name]/>

class Service : public ::zserio::IService
{
public:
    Service();
    virtual ~Service() = default;

    Service(const Service&) = default;
    Service& operator=(const Service&) = default;

    Service(Service&&) = default;
    Service& operator=(Service&&) = default;

    virtual void callMethod(const std::string& methodName, const std::vector<uint8_t>& requestData,
            std::vector<uint8_t>& responseData, void* context = nullptr) override;

    static const char* serviceFullName() noexcept;
    static const ::std::array<const char*, ${methodList?size}>& methodNames() noexcept;

private:
<#if methodList?has_content>
<#list methodList as method>
    virtual void ${method.name}Impl(const ${method.requestTypeFullName}& request, <#rt>
            <#lt>${method.responseTypeFullName}& response, void* context) = 0;
</#list>

<#list methodList as method>
    void ${method.name}Method(const std::vector<uint8_t>& requestData, std::vector<uint8_t>& responseData,
            void* context);
</#list>

</#if>
    using Method = std::function<void(const std::vector<uint8_t>&, std::vector<uint8_t>&, void*)>;
    std::map<std::string, Method> m_methodMap;
};

class Client
{
public:
    explicit Client(::zserio::IService& service);
    ~Client() = default;

    Client(const Client&) = default;
    Client& operator=(const Client&) = default;

    Client(Client&&) = default;
    Client& operator=(Client&&) = default;
<#list methodList as method>

    void ${method.name}Method(${method.requestTypeFullName}& request, <#rt>
            <#lt>${method.responseTypeFullName}& response, void* context = nullptr);
</#list>

private:
    ::zserio::IService& m_service;
};
<@namespace_end [name]/>
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
