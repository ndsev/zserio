<#include "CompoundField.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#macro compound_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}();
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
    <#if !constructorArgumentTypeList?has_content>explicit </#if>${compoundConstructorsData.compoundName}(zserio::BitStreamReader& _in<#rt>
    <#if constructorArgumentTypeList?has_content>
        ,${constructorArgumentTypeList}<#t>
    </#if>
    <#lt>);
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
    <#local hasInitializers=constructorArgumentTypeList?has_content ||
            needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList)/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(zserio::BitStreamReader& _in<#rt>
    <#if constructorArgumentTypeList?has_content>
        ,${constructorArgumentTypeList}<#t>
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
    read(_in);
}
</#macro>

<#macro compound_read_tree_constructor_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    <#if !constructorArgumentTypeList?has_content>explicit </#if>${compoundConstructorsData.compoundName}(const zserio::BlobInspectorTree& _tree<#rt>
    <#if constructorArgumentTypeList?has_content>
        ,${constructorArgumentTypeList}<#t>
    </#if>
    <#lt>);
</#macro>

<#macro compound_read_tree_constructor_definition compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
    <#local hasInitializers=constructorArgumentTypeList?has_content ||
            needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList)/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(const zserio::BlobInspectorTree& _tree<#rt>
    <#if constructorArgumentTypeList?has_content>
        ,${constructorArgumentTypeList}<#t>
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
    read(_tree);
}
</#macro>

<#macro compound_copy_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}(const ${compoundConstructorsData.compoundName}& _other);
</#macro>

<#macro compound_copy_initialization compoundConstructorsData>
    <#if needs_compound_initialization(compoundConstructorsData)>
    if (_other.m_isInitialized)
        initialize(<@compound_initialize_copy_argument_list compoundConstructorsData/>);
    else
        m_isInitialized = false;
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
    if (_other.m_areChildrenInitialized)
        initializeChildren();
    else
        m_areChildrenInitialized = false;
    </#if>
</#macro>

<#macro compound_copy_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
    <#lt>const ${compoundConstructorsData.compoundName}& _other)<#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_copy_constructor_initializer_field field, field_has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
    <@compound_copy_initialization compoundConstructorsData/>
}
</#macro>

<#macro compound_assignment_operator_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}& operator=(const ${compoundConstructorsData.compoundName}& _other);
</#macro>

<#macro compound_assignment_operator_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::operator=(<#rt>
    <#lt>const ${compoundConstructorsData.compoundName}& _other)
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

<#macro compound_initialize_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    void initialize(${constructorArgumentTypeList});
</#macro>

<#macro compound_initialize_definition compoundConstructorsData needsChildrenInitialization>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
void ${compoundConstructorsData.compoundName}::initialize(${constructorArgumentTypeList})
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

<#macro compound_constructor_argument_type_list compoundConstructorsData, indent>
    <@compound_parameter_constructor_type_list compoundConstructorsData.compoundParametersData, indent/>
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
