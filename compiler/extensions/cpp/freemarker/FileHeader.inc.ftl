<#macro file_header generatorDescription>
${generatorDescription}
</#macro>

<#macro camel_case_to_underscores value>
    <#t>${value?replace("(?<=[a-z0-9])[A-Z]", "_$0", "r")?upper_case}
</#macro>

<#macro include_guard_name packagePath typeName>
    <#list packagePath as namespace>
        <#t><@camel_case_to_underscores namespace/>_<#rt>
    </#list>
    <#t><@camel_case_to_underscores typeName/>_H
</#macro>

<#macro user_include packagePath typeName>
    #include <<#t>
    <#list packagePath as namespace>
        <#t>${namespace}/<#rt>
    </#list>
    <#lt>${typeName}>
</#macro>

<#macro system_includes includeFiles>
    <#list includeFiles as include>
#include <${include}>
    </#list>
</#macro>

<#macro user_includes includeFiles autoNewLine=true>
    <#if includeFiles?has_content && autoNewLine>

    </#if>
    <#list includeFiles as include>
#include <${include}>
    </#list>
</#macro>

<#macro type_includes type>
<@system_includes type.systemIncludes/>
<@user_includes type.userIncludes/>
</#macro>

<#macro include_guard_begin packagePath typeName>
#ifndef <@include_guard_name packagePath, typeName/>
#define <@include_guard_name packagePath, typeName/>
</#macro>

<#macro include_guard_end packagePath typeName>
#endif // <@include_guard_name packagePath, typeName/>
</#macro>

<#macro namespace_begin packagePath>
    <#if packagePath?has_content>

        <#list packagePath as namespace>
namespace ${namespace}
{
        </#list>
    </#if>
</#macro>

<#macro namespace_end packagePath>
    <#if packagePath?has_content>

        <#list packagePath?reverse as namespace>
} // namespace ${namespace}
        </#list>
    </#if>
</#macro>

<#macro heap_optional_type_name typeName>
    ${types.heapOptionalHolder.name}<${typeName}<#t>
            <#if types.heapOptionalHolder.needsAllocatorArgument>, ${types.allocator.name}<${typeName}></#if>><#t>
</#macro>

<#macro unique_ptr_type_name typeName>
    ${types.uniquePtr.name}<${typeName}<#t>
            <#if types.uniquePtr.needsAllocatorArgument>, ${types.allocator.name}<${typeName}></#if>><#t>
</#macro>

<#macro vector_type_name typeName>
    ${types.vector.name}<${typeName}<#t>
            <#if types.vector.needsAllocatorArgument>, ${types.allocator.name}<${typeName}></#if>><#t>
</#macro>

<#macro map_type_name keyTypeName valueTypeName>
    ${types.map.name}<${keyTypeName}, ${valueTypeName}<#t>
            <#if types.map.needsAllocatorArgument>, ${types.allocator.name}<${typeName}></#if>><#t>
</#macro>

<#macro set_type_name typeName>
    ${types.set.name}<${typeName}<#t>
            <#if types.set.needsAllocatorArgument>, ${types.allocator.name}<${typeName}></#if>><#t>
</#macro>

<#macro pybind_includes>
#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
</#macro>
