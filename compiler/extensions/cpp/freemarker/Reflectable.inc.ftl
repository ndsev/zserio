<#include "TypeInfo.inc.ftl">
<#macro reflectable_get_field compoundName fieldList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual <#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> getField(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list fieldList as field>
${I}    if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
        <#if field.optional??>
${I}        if (!m_object.${field.optional.isSetIndicatorName}())
${I}            return nullptr;

        </#if>
${I}        return <@reflectable_create_field field/>;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Field '") + name + "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_set_field compoundName fieldList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual void setField(::zserio::StringView name,
${I}        const ::zserio::AnyHolder<allocator_type>& value) override
${I}{
    <#list fieldList as field>
${I}    if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
${I}        m_object.${field.setterName}(value.get<<@field_raw_cpp_type_name field/>>());
${I}        return;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Field '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro reflectable_get_parameter compoundName parametersList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual <#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> getParameter(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list parametersList as parameter>
${I}    if (name == ::zserio::makeStringView("${parameter.name}"))
${I}    {
${I}        return <@reflectable_create_parameter parameter/>;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Parameter '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro reflectable_call_function compoundName functionList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}virtual <#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> callFunction(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list functionList as function>
${I}    if (name == ::zserio::makeStringView("${function.schemaName}"))
${I}    {
${I}        return <@reflectable_create_function function/>;
${I}    }
    </#list>
${I}    else
${I}    {
${I}        throw ::zserio::CppRuntimeException("Function '") + name + "' doesn't exist in '${compoundName}'!";
${I}    }
${I}}
</#macro>

<#macro reflectable_create_field field>
    <#if field.array??>
        <#if field.array.elementCompound??>
            ${types.reflectableFactory.name}::getCompoundArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#elseif field.array.elementTypeInfo.isBitmask>
            ${types.reflectableFactory.name}::getBitmaskArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#elseif field.array.elementTypeInfo.isEnum>
            ${types.reflectableFactory.name}::getEnumArray(<#t>
                    m_object.${field.getterName}(), get_allocator())<#t>
        <#else>
            ${types.reflectableFactory.name}::getBuiltinArray(<#t>
                    <@type_info field.array.elementTypeInfo/>, m_object.${field.getterName}(), <#t>
            <#if field.array.elementObjectIndirectDynamicBitSizeValue??>
                    static_cast<uint8_t>(${field.array.elementObjectIndirectDynamicBitSizeValue}), <#t>
            </#if>
                    get_allocator())<#t>
        </#if>
    <#else>
        <#if field.typeInfo.typeInfoGetter??>
            ${types.reflectableFactory.name}::get${field.typeInfo.typeInfoGetter.suffix}(<#t>
                    <#if field.typeInfo.typeInfoGetter.arg??>${field.typeInfo.typeInfoGetter.arg}, </#if><#t>
                    m_object.${field.getterName}(), <#t>
            <#if field.objectIndirectDynamicBitSizeValue??>
                    static_cast<uint8_t>(${field.objectIndirectDynamicBitSizeValue}), <#t>
            </#if>
                    get_allocator())<#t>
        <#elseif field.typeInfo.isEnum>
            ::zserio::enumReflectable(m_object.${field.getterName}(), get_allocator())<#t>
        <#else>
            m_object.${field.getterName}().reflectable(get_allocator())<#t>
        </#if>
    </#if>
</#macro>

<#macro reflectable_create_parameter parameter>
    <#if parameter.typeInfo.typeInfoGetter??>
        ${types.reflectableFactory.name}::get${parameter.typeInfo.typeInfoGetter.suffix}(<#t>
                <#if parameter.typeInfo.typeInfoGetter.arg??>${parameter.typeInfo.typeInfoGetter.arg}, </#if><#t>
                m_object.${parameter.getterName}(), get_allocator())<#t>
    <#elseif parameter.typeInfo.isEnum>
        ::zserio::enumReflectable(m_object.${parameter.getterName}(), get_allocator())<#t>
    <#else>
        m_object.${parameter.getterName}().reflectable(get_allocator())<#t>
    </#if>
</#macro>

<#macro reflectable_create_function function>
    <#if function.returnTypeInfo.typeInfoGetter??>
        ${types.reflectableFactory.name}::get${function.returnTypeInfo.typeInfoGetter.suffix}(<#t>
                <#if function.returnTypeInfo.typeInfoGetter.arg??>${function.returnTypeInfo.typeInfoGetter.arg}, </#if><#t>
                m_object.${function.name}(), get_allocator())<#t>
    <#elseif function.returnTypeInfo.isEnum>
        ::zserio::enumReflectable(m_object.${function.name}(), get_allocator())<#t>
    <#else>
        m_object.${function.name}().reflectable(get_allocator())<#t>
    </#if>
</#macro>
