<#macro parameter_member_name paramName>
    m_${paramName}_<#t>
</#macro>

<#macro parameter_argument_name paramName>
    ${paramName}_<#t>
</#macro>

<#macro compound_parameter_constructor_initializers compoundParametersData, indent, trailingComma>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterNamePrefix><#if !compoundParameter.typeInfo.isSimple>&</#if></#local>
${I}<@parameter_member_name compoundParameter.name/>(${parameterNamePrefix}<@parameter_argument_name compoundParameter.name/>)<#if compoundParameter?has_next || trailingComma>,</#if>
    </#list>
</#macro>

<#macro compound_parameter_initialize compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
${I}<@parameter_member_name compoundParameter.name/> = <#if !compoundParameter.typeInfo.isSimple>&</#if><@parameter_argument_name compoundParameter.name/>;
    </#list>
</#macro>

<#macro compound_parameter_copy_argument_list compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple>*(</#if>other.<@parameter_member_name compoundParameter.name/><#t>
            <#if !compoundParameter.typeInfo.isSimple>)</#if><#if compoundParameter?has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_parameter_constructor_type_list compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterType>
        <#if !compoundParameter.typeInfo.isSimple>
            <#if withWriterCode>
                ${compoundParameter.typeInfo.typeFullName}&<#t>
            <#else>
                const ${compoundParameter.typeInfo.typeFullName}&<#t>
            </#if>
        <#else>
            ${compoundParameter.typeInfo.typeFullName}<#t>
        </#if>
    </#local>
${I}${parameterType} <@parameter_argument_name compoundParameter.name/><#rt>
        <#if compoundParameter?has_next>
            <#lt>,
        </#if>
    </#list>
</#macro>

<#macro compound_parameter_accessors_declaration compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple>
            <#if withWriterCode>
            <#-- non-const getter is necessary for setting of offsets -->
    ${compoundParameter.typeInfo.typeFullName}& ${compoundParameter.getterName}();
            </#if>
    const ${compoundParameter.typeInfo.typeFullName}& ${compoundParameter.getterName}() const;
        <#else>
    ${compoundParameter.typeInfo.typeFullName} ${compoundParameter.getterName}() const;
        </#if>
    </#list>
    <#if compoundParametersData.list?has_content>

    </#if>
</#macro>

<#macro compound_parameter_accessors_definition compoundName compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple && withWriterCode>
${compoundParameter.typeInfo.typeFullName}& ${compoundName}::${compoundParameter.getterName}()
{
    if (!m_isInitialized)
        throw ::zserio::CppRuntimeException("Parameter '${compoundParameter.name}' of compound '${compoundName}' is not initialized!");

    return *<@parameter_member_name compoundParameter.name/>;
}

        </#if>
        <#if !compoundParameter.typeInfo.isSimple>
const ${compoundParameter.typeInfo.typeFullName}& ${compoundName}::${compoundParameter.getterName}() const
        <#else>
${compoundParameter.typeInfo.typeFullName} ${compoundName}::${compoundParameter.getterName}() const
        </#if>
{
    if (!m_isInitialized)
        throw ::zserio::CppRuntimeException("Parameter '${compoundParameter.name}' of compound '${compoundName}' is not initialized!");

    return <#if !compoundParameter.typeInfo.isSimple>*</#if><@parameter_member_name compoundParameter.name/>;
}

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#-- parameters can't be const for operator=() to work and initialize() needs to update them too -->
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterCppTypeName>
        <#if compoundParameter.typeInfo.isSimple>
            ${compoundParameter.typeInfo.typeFullName}<#t>
        <#else>
            <#if !withWriterCode>const </#if>${compoundParameter.typeInfo.typeFullName}*<#t>
        </#if>
    </#local>
    ${parameterCppTypeName} <@parameter_member_name compoundParameter.name/>;
    </#list>
</#macro>

<#macro compound_parameter_comparison compoundParametersData, trailingAnd>
    <#list compoundParametersData.list as compoundParameter>
                (${compoundParameter.getterName}() == other.${compoundParameter.getterName}())<#if compoundParameter?has_next || trailingAnd> &&<#else>;</#if>
    </#list>
</#macro>

<#macro compound_parameter_comparison_with_any_holder compoundParametersData>
    <#if compoundParametersData.list?has_content>
    if (<#rt>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter?is_first>            </#if>!(${compoundParameter.getterName}() == other.${compoundParameter.getterName}())<#t>
        <#if compoundParameter?has_next>
            <#lt>||
        </#if>
    </#list>
    <#lt>)
        return false;

    </#if>
</#macro>

<#macro compound_parameter_hash_code compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    result = ::zserio::calcHashCode(result, ${compoundParameter.getterName}());
    </#list>
</#macro>
