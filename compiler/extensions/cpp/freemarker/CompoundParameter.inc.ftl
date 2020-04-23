<#macro parameter_member_name param>
    m_${param.name}_<#t>
</#macro>

<#macro parameter_argument_name param>
    ${param.name}_<#t>
</#macro>

<#macro compound_parameter_constructor_initializers compoundParametersData, indent, trailingComma>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterNamePrefix><#if !compoundParameter.isSimpleType>&</#if></#local>
${I}<@parameter_member_name compoundParameter/>(${parameterNamePrefix}<@parameter_argument_name compoundParameter/>)<#if compoundParameter?has_next || trailingComma>,</#if>
    </#list>
</#macro>

<#macro compound_parameter_initialize compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
${I}<@parameter_member_name compoundParameter/> = <#if !compoundParameter.isSimpleType>&</#if><@parameter_argument_name compoundParameter/>;
    </#list>
</#macro>

<#macro compound_parameter_copy_argument_list compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.isSimpleType>*(</#if>other.<@parameter_member_name compoundParameter/><#t>
            <#if !compoundParameter.isSimpleType>)</#if><#if compoundParameter?has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_parameter_constructor_type_list compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterType><#if compoundParametersData.withWriterCode && !compoundParameter.isSimpleType><#rt>
        <#lt>${compoundParameter.cppTypeName}&<#else>${compoundParameter.cppArgumentTypeName}</#if></#local>
${I}${parameterType} <@parameter_argument_name compoundParameter/><#rt>
        <#if compoundParameter?has_next>
            <#lt>,
        </#if>
    </#list>
</#macro>

<#macro compound_parameter_accessors_declaration compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if compoundParametersData.withWriterCode && !compoundParameter.isSimpleType>
            <#-- non-const getter is necessary for setting of offsets -->
    ${compoundParameter.cppTypeName}& ${compoundParameter.getterName}();
        </#if>
    ${compoundParameter.cppArgumentTypeName} ${compoundParameter.getterName}() const;
    </#list>
    <#if compoundParametersData.list?has_content>

    </#if>
</#macro>

<#macro compound_parameter_accessors_definition compoundName compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if compoundParametersData.withWriterCode && !compoundParameter.isSimpleType>
${compoundParameter.cppTypeName}& ${compoundName}::${compoundParameter.getterName}()
{
    if (!m_isInitialized)
        throw ::zserio::CppRuntimeException("Parameter ${compoundParameter.name} of compound ${compoundName} "
                "is not initialized!");

    return *<@parameter_member_name compoundParameter/>;
}

        </#if>
${compoundParameter.cppArgumentTypeName} ${compoundName}::${compoundParameter.getterName}() const
{
    if (!m_isInitialized)
        throw ::zserio::CppRuntimeException("Parameter ${compoundParameter.name} of compound ${compoundName} "
                "is not initialized!");

    return <#if !compoundParameter.isSimpleType>*</#if><@parameter_member_name compoundParameter/>;
}

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#-- parameters can't be const for operator=() to work and initialize() needs to update them too -->
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterCppTypeName><#if compoundParameter.isSimpleType>${compoundParameter.cppArgumentTypeName}<#else><#rt>
        <#lt><#if !compoundParametersData.withWriterCode>const </#if>${compoundParameter.cppTypeName}*</#if></#local>
    ${parameterCppTypeName} <@parameter_member_name compoundParameter/>;
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
