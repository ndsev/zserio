<#include "CompoundField.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#macro compound_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}() noexcept;
</#macro>

<#macro compound_constructor_members_initialization compoundConstructorsData>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_isInitialized(false)<#t>
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(false)<#t>
    </#if>
</#macro>

<#macro compound_read_constructor_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    explicit ${compoundConstructorsData.compoundName}(zserio::BitStreamReader& in<#rt>
    <#if constructorArgumentTypeList?has_content>
            <#lt>,
            ${constructorArgumentTypeList}<#t>
    </#if>
    <#lt>);
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
    <#local hasInitializers=constructorArgumentTypeList?has_content ||
            needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList)/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(zserio::BitStreamReader& in<#rt>
    <#if constructorArgumentTypeList?has_content>
        <#lt>,
        ${constructorArgumentTypeList}<#t>
    </#if>
    <#if hasInitializers>
        <#lt>) :
    <#else>
        <#lt>)
    </#if>
    <#if constructorArgumentTypeList?has_content>
        <@compound_parameter_constructor_initializers compoundConstructorsData.compoundParametersData, 2,
                needs_compound_initialization(compoundConstructorsData)/>
    </#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_isInitialized(true)
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(true)
    </#if>
{
    read(in);
}
</#macro>

<#macro compound_fields_constructor compoundConstructorsData>
    <#if compoundConstructorsData.fieldList?has_content || needs_compound_initialization(compoundConstructorsData)>

    <@compound_field_constructor_template_arg_list compoundConstructorsData.fieldList/>
    explicit ${compoundConstructorsData.compoundName}(<#rt>
            <#lt><@compound_fields_constructor_argument_type_list compoundConstructorsData, 3/>) :
        <#if needs_compound_initialization(compoundConstructorsData)>
            <@compound_parameter_constructor_initializers compoundConstructorsData.compoundParametersData, 3, true/>
            m_isInitialized(true)<#if compoundConstructorsData.fieldList?has_content>, </#if>
        </#if>
        <#list compoundConstructorsData.fieldList as field>
            <@compound_field_constructor_initializer_field field, field?has_next, 3/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    {
        <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        initializeChildren();
        </#if>
    }
    </#if>
</#macro>

<#macro compound_copy_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}(const ${compoundConstructorsData.compoundName}& other);
</#macro>

<#macro compound_copy_initialization compoundConstructorsData>
    <#if needs_compound_initialization(compoundConstructorsData)>
    if (other.m_isInitialized)
        initialize(<@compound_initialize_copy_argument_list compoundConstructorsData/>);
    else
        m_isInitialized = false;
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
    if (other.m_areChildrenInitialized)
        initializeChildren();
    else
        m_areChildrenInitialized = false;
    </#if>
</#macro>

<#macro compound_copy_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
    <#lt>const ${compoundConstructorsData.compoundName}& other)<#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_copy_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
    <@compound_copy_initialization compoundConstructorsData/>
}
</#macro>

<#macro compound_assignment_operator_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}& operator=(const ${compoundConstructorsData.compoundName}& other);
</#macro>

<#macro compound_assignment_operator_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::operator=(<#rt>
    <#lt>const ${compoundConstructorsData.compoundName}& other)
{
    <#list compoundConstructorsData.fieldList as field>
        <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}
</#macro>

<#macro compound_move_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
    <#lt>${compoundConstructorsData.compoundName}&& other)<#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_move_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
    <@compound_copy_initialization compoundConstructorsData/>
}
</#macro>

<#macro compound_move_assignment_operator_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}& operator=(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_assignment_operator_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::operator=(<#rt>
    <#lt>${compoundConstructorsData.compoundName}&& other)
{
    <#list compoundConstructorsData.fieldList as field>
        <@compound_move_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}
</#macro>

<#macro compound_initialize_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    void initialize(<#rt>
    <#if constructorArgumentTypeList?has_content>

    </#if>
            <#lt>${constructorArgumentTypeList});
</#macro>

<#macro compound_initialize_definition compoundConstructorsData needsChildrenInitialization>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
void ${compoundConstructorsData.compoundName}::initialize(
        <#lt>${constructorArgumentTypeList})
{
    <@compound_parameter_initialize compoundConstructorsData.compoundParametersData, 1/>
    m_isInitialized = true;
    <#if needsChildrenInitialization>

    initializeChildren();
    </#if>
}
</#macro>

<#macro compound_initialize_children_epilog_definition compoundConstructorsData>
    <#if !needs_compound_initialization(compoundConstructorsData) &&
            has_field_with_initialization(compoundConstructorsData.fieldList)>

    m_areChildrenInitialized = true;
    </#if>
</#macro>

<#macro compound_constructor_argument_type_list compoundConstructorsData indent>
    <@compound_parameter_constructor_type_list compoundConstructorsData.compoundParametersData, indent/>
</#macro>

<#macro compound_fields_constructor_argument_type_list compoundConstructorsData indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local parameterTypeList>
        <@compound_parameter_constructor_type_list compoundConstructorsData.compoundParametersData, indent/>
    </#local>
    <#local fieldTypeList>
        <@compound_field_constructor_type_list compoundConstructorsData.fieldList, indent/>
    </#local>

    <#if parameterTypeList?has_content>
        ${parameterTypeList}<#t>
        <#if fieldTypeList?has_content>
            <#lt>,
        </#if>
    </#if>
    <#if fieldTypeList?has_content>
        ${fieldTypeList}<#t>
    </#if>
</#macro>

<#macro compound_initialize_copy_argument_list compoundConstructorsData>
    <@compound_parameter_copy_argument_list compoundConstructorsData.compoundParametersData/><#rt>
</#macro>

<#macro compound_constructor_members compoundConstructorsData>
    <#if needs_compound_initialization(compoundConstructorsData)>
    bool m_isInitialized;
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
    bool m_areChildrenInitialized;
    </#if>
</#macro>

<#function needs_compound_initialization compoundConstructorsData>
    <#if compoundConstructorsData.compoundParametersData.list?has_content>
        <#return true>
    </#if>
    <#return false>
</#function>
