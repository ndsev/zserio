<#include "DocComment.inc.ftl">
<#macro parameter_member_name paramName>
    m_${paramName}_<#t>
</#macro>

<#macro parameter_argument_name paramName>
    ${paramName}_<#t>
</#macro>

<#macro parameter_view_type parameter>
    <#if parameter.typeInfo.isCompound>
        ${parameter.typeInfo.typeFullName}::View<#t>
    <#else>
        ${parameter.typeInfo.typeFullName}<#t>
    </#if>
</#macro>

<#macro compound_parameter_view_members compoundParametersData>
    <#-- parameters can't be const for operator=() to work and initialize() needs to update them too -->
    <#list compoundParametersData.list as compoundParameter>
        <@parameter_view_type compoundParameter/> <@parameter_member_name compoundParameter.name/>;
    </#list>
</#macro>

<#macro compound_parameter_view_constructor_type_list compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
        <#local parameterType>
            <#if compoundParameter.typeInfo.isCompound>
                ${compoundParameter.typeInfo.typeFullName}::View<#t>
            <#else>
                ${compoundParameter.typeInfo.typeFullName}<#t>
            </#if>
        </#local>
<#if !compoundParameter?is_first>${I}</#if>${parameterType} <@parameter_argument_name compoundParameter.name/>,
    </#list>
</#macro>

<#macro compound_parameter_view_constructor_initializers compoundParametersData, indent, trailingComma>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
${I}<@parameter_member_name compoundParameter.name/>(<@parameter_argument_name compoundParameter.name/>)<#if compoundParameter?has_next || trailingComma>,</#if>
    </#list>
</#macro>

<#macro compound_parameter_view_accessors compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>

        <#if compoundParameter.typeInfo.isCompound>
        ${compoundParameter.typeInfo.typeFullName}::View ${compoundParameter.getterName}() const
        <#else>
        ${compoundParameter.typeInfo.typeFullName} ${compoundParameter.getterName}() const
        </#if>
        {
            return <@parameter_member_name compoundParameter.name/>;
        }
    </#list>
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
                <#if withCodeComments>
    /**
     * Gets the reference to the parameter ${compoundParameter.name}.
     *
     * This getter can be called internally during setting of the offsets.
     *
                    <#if compoundParameter.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundParameter.docComments, 1/>
     *
                    </#if>
     * \return The reference to the parameter ${compoundParameter.name}.
     */
                </#if>
    ${compoundParameter.typeInfo.typeFullName}& ${compoundParameter.getterName}();
            </#if>
            <#if withCodeComments>

    /**
     * Gets the const reference to the parameter ${compoundParameter.name}.
     *
                <#if compoundParameter.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundParameter.docComments, 1/>
     *
                </#if>
     * \return The const reference to the parameter ${compoundParameter.name}.
     */
            </#if>
    const ${compoundParameter.typeInfo.typeFullName}& ${compoundParameter.getterName}() const;
        <#else>
            <#if withCodeComments>
    /**
     * Gets the value of the parameter ${compoundParameter.name}.
     *
                <#if compoundParameter.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundParameter.docComments, 1/>
     *
                </#if>
     * \return The value of the parameter ${compoundParameter.name}.
     */
            </#if>
    ${compoundParameter.typeInfo.typeFullName} ${compoundParameter.getterName}() const;
        </#if>
    </#list>
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

<#macro compound_parameter_less_than_compare compoundParamter lhs rhs>
    <#if compoundParamter.typeInfo.isBoolean>
        static_cast<int>(${lhs}) < static_cast<int>(${rhs})<#t>
    <#else>
        ${lhs} < ${rhs}<#t>
    </#if>
</#macro>

<#macro compound_parameter_less_than compoundParametersData indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
        <#local lhs>${compoundParameter.getterName}()</#local>
        <#local rhs>other.${compoundParameter.getterName}()</#local>
${I}if (<@compound_parameter_less_than_compare compoundParameter, lhs, rhs/>)
${I}    return true;
${I}if (<@compound_parameter_less_than_compare compoundParameter, rhs, lhs/>)
${I}    return false;

    </#list>
</#macro>

<#macro compound_parameter_hash_code compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    result = ::zserio::calcHashCode(result, ${compoundParameter.getterName}());
    </#list>
</#macro>

<#function has_non_simple_parameter compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
