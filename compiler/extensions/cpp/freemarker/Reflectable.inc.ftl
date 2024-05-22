<#include "TypeInfo.inc.ftl">
<#macro reflectable_initialize_children needsChildrenInitialization indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}void initializeChildren() override
${I}{
    <#if needsChildrenInitialization>
${I}    m_object.initializeChildren();
    </#if>
${I}}
</#macro>

<#macro reflectable_initialize compoundName parameterList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}void initialize(
${I}        const ::zserio::vector<::zserio::AnyHolder<allocator_type>, allocator_type>& typeArguments) override
${I}{
${I}    if (typeArguments.size() != ${parameterList?size})
${I}    {
${I}        throw ::zserio::CppRuntimeException("Not enough arguments to ${name}::initialize, ") <<
${I}                "expecting ${parameterList?size}, got " << typeArguments.size();
${I}    }

${I}    m_object.initialize(
    <#list parameterList as parameter>
${I}        typeArguments[${parameter?index}].get<<#rt>
        <#if parameter.typeInfo.isSimple>
            ${parameter.typeInfo.typeFullName}>()<#t>
        <#else>
            ::std::reference_wrapper<${parameter.typeInfo.typeFullName}>>().get()<#t>
        </#if>
        <#if parameter?has_next>
                <#lt>,
        <#else>

        </#if>
    </#list>
${I}    );
${I}}
</#macro>

<#macro reflectable_get_field compoundName fieldList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}<#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> getField(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list fieldList as field>
${I}    if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
        <#if field.isExtended>
${I}        if (!m_object.${field.isPresentIndicatorName}())
${I}        {
${I}            return nullptr;
${I}        }
        </#if>
        <#if field.optional??>
            <#if withWriterCode>
${I}        if (!m_object.${field.optional.isSetIndicatorName}())
            <#else>
${I}        if (!m_object.${field.optional.isUsedIndicatorName}())
            </#if>
${I}        {
${I}            return nullptr;
${I}        }

        </#if>
${I}        return <@reflectable_field_create field/>;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_create_field compoundName fieldList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}${types.reflectablePtr.name} createField(::zserio::StringView name) override
${I}{
    <#list fieldList as field>
${I}    if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
${I}        m_object.${field.setterName}(<@field_raw_cpp_type_name field/>(<#rt>
                    <#lt><#if field.needsAllocator>get_allocator()</#if>));
${I}        return <@reflectable_field_create field/>;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_set_field compoundName fieldList indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}void setField(::zserio::StringView name,
${I}        const ::zserio::AnyHolder<allocator_type>& value) override
${I}{
    <#list fieldList as field>
${I}    if (name == ::zserio::makeStringView("${field.name}"))
${I}    {
        <#if field.optional??>
${I}        if (value.isType<::std::nullptr_t>())
${I}        {
${I}            m_object.${field.optional.resetterName}();
${I}            return;
${I}        }

        </#if>
        <#if field.typeInfo.isEnum || field.typeInfo.isBitmask>
${I}        if (value.isType<<@field_raw_cpp_type_name field/>>())
${I}        {
${I}            m_object.${field.setterName}(value.get<<@field_raw_cpp_type_name field/>>());
${I}        }
${I}        else
${I}        {
            <#if field.typeInfo.isEnum>
${I}            m_object.${field.setterName}(::zserio::valueToEnum<<@field_raw_cpp_type_name field/>>(
${I}                    value.get<typename ::std::underlying_type<<@field_raw_cpp_type_name field/>>::type>()));
            <#else><#-- bitmask -->
${I}            m_object.${field.setterName}(<@field_raw_cpp_type_name field/>(
${I}                    value.get<<@field_raw_cpp_type_name field/>::underlying_type>()));
            </#if>
${I}        }
        <#else>
${I}        m_object.${field.setterName}(value.get<<@field_raw_cpp_type_name field/>>());
        </#if>
${I}        return;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Field '") << name << "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_get_parameter compoundName parametersList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}<#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> getParameter(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list parametersList as parameter>
${I}    if (name == ::zserio::makeStringView("${parameter.name}"))
${I}    {
${I}        return <@reflectable_parameter_create parameter/>;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Parameter '") << name << "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_call_function compoundName functionList isConst indent=2>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}<#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> callFunction(<#rt>
        <#lt>::zserio::StringView name) <#if isConst>const </#if>override
${I}{
    <#list functionList as function>
${I}    if (name == ::zserio::makeStringView("${function.schemaName}"))
${I}    {
${I}        return <@reflectable_function_create function/>;
${I}    }
    </#list>
${I}    throw ::zserio::CppRuntimeException("Function '") << name << "' doesn't exist in '${compoundName}'!";
${I}}
</#macro>

<#macro reflectable_field_create field>
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
            ${types.reflectableFactory.name}::get${field.array.elementTypeInfo.typeInfoGetter.suffix}Array(<#t>
                    m_object.${field.getterName}(), <#t>
                    <#if field.array.elementTypeInfo.typeInfoGetter.arg??>${field.array.elementTypeInfo.typeInfoGetter.arg}, </#if><#t>
            <#if field.array.elementBitSize?? && field.array.elementBitSize.objectIndirectValue??>
                    static_cast<uint8_t>(${field.array.elementBitSize.objectIndirectValue}), <#t>
            </#if>
                    get_allocator())<#t>
        </#if>
    <#else>
        <#if field.typeInfo.typeInfoGetter??>
            ${types.reflectableFactory.name}::get${field.typeInfo.typeInfoGetter.suffix}(<#t>
                    m_object.${field.getterName}(), <#t>
                    <#if field.typeInfo.typeInfoGetter.arg??>${field.typeInfo.typeInfoGetter.arg}, </#if><#t>
            <#if field.bitSize?? && field.bitSize.objectIndirectValue??>
                    static_cast<uint8_t>(${field.bitSize.objectIndirectValue}), <#t>
            </#if>
                    get_allocator())<#t>
        <#elseif field.typeInfo.isEnum>
            ::zserio::enumReflectable(m_object.${field.getterName}(), get_allocator())<#t>
        <#else>
            m_object.${field.getterName}().reflectable(get_allocator())<#t>
        </#if>
    </#if>
</#macro>

<#macro reflectable_parameter_create parameter>
    <#if parameter.typeInfo.typeInfoGetter??>
        ${types.reflectableFactory.name}::get${parameter.typeInfo.typeInfoGetter.suffix}(<#t>
                m_object.${parameter.getterName}(), <#t>
                <#if parameter.typeInfo.typeInfoGetter.arg??>${parameter.typeInfo.typeInfoGetter.arg}, </#if><#t>
                get_allocator())<#t>
    <#elseif parameter.typeInfo.isEnum>
        ::zserio::enumReflectable(m_object.${parameter.getterName}(), get_allocator())<#t>
    <#else>
        m_object.${parameter.getterName}().reflectable(get_allocator())<#t>
    </#if>
</#macro>

<#macro reflectable_function_create function>
    <#if function.returnTypeInfo.typeInfoGetter??>
        ${types.reflectableFactory.name}::get${function.returnTypeInfo.typeInfoGetter.suffix}(<#t>
                m_object.${function.name}(), <#t>
                <#if function.returnTypeInfo.typeInfoGetter.arg??>${function.returnTypeInfo.typeInfoGetter.arg}, </#if><#t>
                get_allocator())<#t>
    <#elseif function.returnTypeInfo.isEnum>
        ::zserio::enumReflectable(m_object.${function.name}(), get_allocator())<#t>
    <#else>
        m_object.${function.name}().reflectable(get_allocator())<#t>
    </#if>
</#macro>
