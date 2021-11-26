<#include "TypeInfo.inc.ftl">
<#macro introspectable_get_field compoundName fieldList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual ${types.introspectablePtr.name} getField(::zserio::StringView name) const override
${I}{
    <#list fieldList as field>
${I}    <#if !field?is_first>else </#if>if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
        <#if field.optional??>
${I}        if (!m_object.${field.optional.indicatorName}())
${I}            return nullptr;

        </#if>
${I}        return <@introspectable_create_field field/>;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Field '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro introspectable_set_field compoundName fieldList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual void setField(::zserio::StringView name,
${I}        const ::zserio::AnyHolder<allocator_type>& value) override
${I}{
    <#list fieldList as field>
${I}    <#if !field?is_first>else </#if>if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
${I}        m_object.${field.setterName}(value.get<<@field_raw_cpp_type_name field/>>());
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Field '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro introspectable_get_parameter compoundName parametersList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual ${types.introspectablePtr.name} getParameter(::zserio::StringView name) const override
${I}{
    <#list parametersList as parameter>
${I}    if (name == ::zserio::makeStringView("${parameter.name}"))
${I}    {
${I}        return <@introspectable_create_parameter parameter/>;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Parameter '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro introspectable_call_function compoundName functionList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual ${types.introspectablePtr.name} callFunction(::zserio::StringView name) const override
${I}{
    <#list functionList as function>
${I}    if (name == ::zserio::makeStringView("${function.schemaName}"))
${I}    {
${I}        return <@introspectable_create_function function/>;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Function '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro introspectable_create_field field>
    <#if field.array??>
        <#if field.array.elementCompound??>
            ${types.introspectableFactory.name}::getCompoundArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#elseif field.typeInfo.isBitmask>
            ${types.introspectableFactory.name}::getBitmaskArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#elseif field.typeInfo.isEnum>
            ${types.introspectableFactory.name}::getEnumArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#else>
            ${types.introspectableFactory.name}::getBuiltinArray(<#t>
                    <@type_info field.typeInfo/>, m_object.${field.getterName}(), <#t>
            <#if field.array.elementObjectIndirectDynamicBitSizeValue??>
                    static_cast<uint8_t>(${field.array.elementObjectIndirectDynamicBitSizeValue}), <#t>
            </#if>
                    get_allocator())<#t>
        </#if>
    <#else>
        <#if field.typeInfo.typeInfoGetter??>
            ${types.introspectableFactory.name}::get${field.typeInfo.typeInfoGetter.suffix}(<#t>
                    <#if field.typeInfo.typeInfoGetter.arg??>${field.typeInfo.typeInfoGetter.arg}, </#if><#t>
                    m_object.${field.getterName}(), <#t>
            <#if field.objectIndirectDynamicBitSizeValue??>
                    static_cast<uint8_t>(${field.objectIndirectDynamicBitSizeValue}), <#t>
            </#if>
                    get_allocator())<#t>
        <#elseif field.typeInfo.isEnum>
            ::zserio::enumIntrospectable(m_object.${field.getterName}(), get_allocator())<#t>
        <#else>
            m_object.${field.getterName}().introspectable(get_allocator())<#t>
        </#if>
    </#if>
</#macro>

<#macro introspectable_create_parameter parameter>
    <#if parameter.typeInfo.typeInfoGetter??>
        ${types.introspectableFactory.name}::get${parameter.typeInfo.typeInfoGetter.suffix}(<#t>
                <#if parameter.typeInfo.typeInfoGetter.arg??>${parameter.typeInfo.typeInfoGetter.arg}, </#if><#t>
                m_object.${parameter.getterName}(), get_allocator())<#t>
    <#elseif parameter.typeInfo.isEnum>
        ::zserio::enumIntrospectable(m_object.${parameter.getterName}(), get_allocator())<#t>
    <#else>
        m_object.${parameter.getterName}().introspectable(get_allocator())<#t>
    </#if>
</#macro>

<#macro introspectable_create_function function>
    <#if function.returnTypeInfo.typeInfoGetter??>
        ${types.introspectableFactory.name}::get${function.returnTypeInfo.typeInfoGetter.suffix}(<#t>
                <#if function.returnTypeInfo.typeInfoGetter.arg??>${function.returnTypeInfo.typeInfoGetter.arg}, </#if><#t>
                m_object.${function.name}(), get_allocator())<#t>
    <#elseif function.returnTypeInfo.isEnum>
        ::zserio::enumIntrospectable(m_object.${function.name}(), get_allocator())<#t>
    <#else>
        m_object.${function.name}().introspectable(get_allocator())<#t>
    </#if>
</#macro>
