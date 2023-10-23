<#include "DocComment.inc.ftl">
<#macro parameter_member_name paramName>
    m_${paramName}_<#t>
</#macro>

<#macro parameter_argument_name paramName>
    ${paramName}_<#t>
</#macro>

<#macro compound_parameter_declare_parameter_expressions compoundParametersData>
    class ParameterExpressions
    {
    public:
    <#list compoundParametersData.list as parameter>
        using ParameterExpression${parameter.name?cap_first} = <#rt>
                <#lt>${parameter.typeInfo.typeFullName}<#if !parameter.typeInfo.isSimple>&</#if> (*)(void*, size_t);
    </#list>

        ParameterExpressions();

        ParameterExpressions(void* owner, size_t index,
    <#list compoundParametersData.list as parameter>
                ParameterExpression${parameter.name?cap_first} <@parameter_argument_name parameter.name/><#rt>
        <#if parameter?has_next>
                <#lt>,
        <#else>
                <#lt>);
        </#if>
    </#list>

    <#list compoundParametersData.list as parameter>
        ${parameter.typeInfo.typeFullName}<#if !parameter.typeInfo.isSimple>&</#if> <#rt>
                <#lt>${parameter.getterName}() const;
    </#list>

        bool isInitialized() const
        {
            <#-- m_owner could have been nullptr if it's not needed in expressions, thus use first paramter instead -->
            return <@parameter_member_name compoundParametersData.list[0].name/> != nullptr;
        }

    private:
        void* m_owner;
        size_t m_index;
    <#list compoundParametersData.list as parameter>
        ParameterExpression${parameter.name?cap_first} <@parameter_member_name parameter.name/>;
    </#list>
    };
</#macro>

<#macro compound_parameter_define_parameter_expressions_methods compoundName compoundParametersData>
${compoundName}::ParameterExpressions::ParameterExpressions():
        m_owner(nullptr), m_index(0),
    <#list compoundParametersData.list as parameter>
        <@parameter_member_name parameter.name/>(nullptr)<#if parameter?has_next>,</#if>
    </#list>
{
}

${compoundName}::ParameterExpressions::ParameterExpressions(void* owner, size_t index,
    <#list compoundParametersData.list as parameter>
        ParameterExpression${parameter.name?cap_first} <@parameter_argument_name parameter.name/><#rt>
        <#if parameter?has_next>
            <#lt>,
        <#else>
            <#lt>) :
        </#if>
    </#list>
        m_owner(owner), m_index(index),
    <#list compoundParametersData.list as parameter>
        <@parameter_member_name parameter.name/>(<@parameter_argument_name parameter.name/>)<#if parameter?has_next>,</#if>
    </#list>
{
}
    <#list compoundParametersData.list as parameter>

${parameter.typeInfo.typeFullName}<#if !parameter.typeInfo.isSimple>&</#if> <#rt>
        <#lt>${compoundName}::ParameterExpressions::${parameter.getterName}() const
{
    return <@parameter_member_name parameter.name/>(m_owner, m_index);
}
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
    if (!isInitialized())
        throw ::zserio::CppRuntimeException("Compound '${compoundName}' is not initialized!");

    return m_parameterExpressions.${compoundParameter.getterName}();
}

        </#if>
        <#if !compoundParameter.typeInfo.isSimple>
const ${compoundParameter.typeInfo.typeFullName}& ${compoundName}::${compoundParameter.getterName}() const
        <#else>
${compoundParameter.typeInfo.typeFullName} ${compoundName}::${compoundParameter.getterName}() const
        </#if>
{
    if (!isInitialized())
        throw ::zserio::CppRuntimeException("Compound '${compoundName}' is not initialized!");

    return m_parameterExpressions.${compoundParameter.getterName}();
}

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

<#function has_non_simple_parameter compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
