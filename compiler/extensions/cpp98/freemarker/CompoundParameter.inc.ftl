<#macro compound_parameter_constructor_initializers compoundParametersData, indent, trailingComma>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterNamePrefix><#if !compoundParameter.isSimpleType>&</#if></#local>
${I}m_${compoundParameter.name}(${parameterNamePrefix}${compoundParameter.name})<#if compoundParameter_has_next || trailingComma>,</#if>
    </#list>
</#macro>

<#macro compound_parameter_initialize compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
${I}m_${compoundParameter.name} = <#if !compoundParameter.isSimpleType>&</#if>${compoundParameter.name};
    </#list>
</#macro>

<#macro compound_parameter_copy_argument_list compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.isSimpleType>*(</#if>_other.m_${compoundParameter.name}<#t>
            <#if !compoundParameter.isSimpleType>)</#if><#if compoundParameter_has_next>, </#if><#t>
    </#list>
</#macro>

<#macro compound_parameter_constructor_type_list compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>

    <#local parameterType><#if compoundParametersData.withWriterCode && !compoundParameter.isSimpleType><#rt>
        ${compoundParameter.cppTypeName}&<#else>${compoundParameter.cppArgumentTypeName}</#if></#local>
${I}${parameterType} ${compoundParameter.name}<#if compoundParameter_has_next>,</#if><#rt>
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
        throw zserio::CppRuntimeException("Parameter ${compoundParameter.name} of compound ${compoundName} "
                "is not initialized!");

    return *m_${compoundParameter.name};
}

        </#if>
${compoundParameter.cppArgumentTypeName} ${compoundName}::${compoundParameter.getterName}() const
{
    if (!m_isInitialized)
        throw zserio::CppRuntimeException("Parameter ${compoundParameter.name} of compound ${compoundName} "
                "is not initialized!");

    return <#if !compoundParameter.isSimpleType>*</#if>m_${compoundParameter.name};
}

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#-- parameters can't be const for operator=() to work and initialize() needs to update them too -->
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterCppTypeName><#if compoundParameter.isSimpleType>${compoundParameter.cppArgumentTypeName}<#else><#rt>
        <#lt><#if !compoundParametersData.withWriterCode>const </#if>${compoundParameter.cppTypeName}*</#if></#local>
    ${parameterCppTypeName} m_${compoundParameter.name};
    </#list>
</#macro>

<#macro compound_parameter_comparison compoundParametersData, trailingAnd>
    <#list compoundParametersData.list as compoundParameter>
                (${compoundParameter.getterName}() == _other.${compoundParameter.getterName}())<#if compoundParameter_has_next || trailingAnd> &&<#else>;</#if>
    </#list>
</#macro>

<#macro compound_parameter_hash_code compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    _result = zserio::calcHashCode(_result, ${compoundParameter.getterName}());
    </#list>
</#macro>
