<#include "CompoundField.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#macro compound_constructor_declaration compoundConstructorsData>
    explicit ${compoundConstructorsData.compoundName}(const allocator_type& allocator = allocator_type()) noexcept;
</#macro>

<#macro compound_constructor_definition compoundConstructorsData memberInitializationMacroName="">
    <#local hasInitializers= needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList) ||
            memberInitializationMacroName != ""/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>const allocator_type&<#rt>
        <#lt><#if empty_constructor_needs_allocator(compoundConstructorsData.fieldList)> allocator</#if>) <#rt>
        <#lt>noexcept<#if hasInitializers> :</#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_isInitialized(false)<#if memberInitializationMacroName != "">,</#if>
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(false)<#if memberInitializationMacroName != "">,</#if>
    </#if>
    <#if memberInitializationMacroName != "">
        <@.vars[memberInitializationMacroName]/>
    </#if>
{
}
</#macro>

<#macro compound_read_constructor_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    explicit ${compoundConstructorsData.compoundName}(::zserio::BitStreamReader& in<#rt>
    <#if constructorArgumentTypeList?has_content>
            <#lt>,
            ${constructorArgumentTypeList}<#t>
    </#if>
    <#lt>, const allocator_type& allocator = allocator_type());
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData memberInitializationMacroName>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
    <#local hasInitializers=constructorArgumentTypeList?has_content ||
            needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList) ||
            memberInitializationMacroName != ""/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>::zserio::BitStreamReader&<#if compoundConstructorsData.fieldList?has_content> in</#if><#rt>
    <#if constructorArgumentTypeList?has_content>
        <#lt>,
        ${constructorArgumentTypeList}<#t>
    </#if>
        , const allocator_type&<#if read_constructor_needs_allocator(compoundConstructorsData.fieldList)> allocator</#if><#t>
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
        m_isInitialized(true)<#if memberInitializationMacroName != "">,</#if>
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(true)<#if memberInitializationMacroName != "">,</#if>
    </#if>
    <#if memberInitializationMacroName != "">
        <@.vars[memberInitializationMacroName]/>
    </#if>
{
}
</#macro>

<#macro compound_copy_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}(const ${compoundConstructorsData.compoundName}& other);
</#macro>

<#macro compound_copy_constructor_allocator_declaration compoundConstructorsData>
    <@compound_copy_constructor_declaration compoundConstructorsData true/>
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

<#macro compound_copy_constructor_allocator_definition compoundConstructorsData>
    <@compound_copy_constructor_definition compoundConstructorsData true/>
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

<#macro compound_allocator_propagating_copy_constructor_declaration compoundConstructorsData>
    ${compoundConstructorsData.compoundName}(::zserio::PropagateAllocatorT,
            const ${compoundConstructorsData.compoundName}& other, const allocator_type& allocator);
</#macro>

<#macro compound_allocator_propagating_copy_constructor_definition compoundConstructorsData>
    <#local initialization><@compound_copy_initialization compoundConstructorsData/></#local>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>::zserio::PropagateAllocatorT,
        const ${compoundConstructorsData.compoundName}&<#rt>
        <#lt><#if compoundConstructorsData.fieldList?has_content || initialization?has_content> other</#if>,<#rt>
        <#lt> const allocator_type&<#if compoundConstructorsData.fieldList?has_content> allocator</#if>)<#rt>
        <#lt><#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_allocator_propagating_copy_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
    <#if initialization?has_content>
    ${initialization}<#t>
    </#if>
}
</#macro>

<#macro compound_initialize_declaration compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    void initialize(<#rt>
    <#if constructorArgumentTypeList?has_content>

    </#if>
            <#lt>${constructorArgumentTypeList});
    bool isInitialized() const;
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

bool ${compoundConstructorsData.compoundName}::isInitialized() const
{
    return m_isInitialized;
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

<#function empty_constructor_needs_allocator fieldList>
    <#list fieldList as field>
        <#if field.usesAnyHolder || field.optional??>
            <#if field.holderNeedsAllocator><#return true></#if>
        <#else>
            <#if field.needsAllocator><#return true></#if>
        </#if>
    </#list>
    <#return false>
</#function>

<#function read_constructor_needs_allocator fieldList>
    <#list fieldList as field>
        <#if field.holderNeedsAllocator || field.needsAllocator>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function needs_compound_initialization compoundConstructorsData>
    <#if compoundConstructorsData.compoundParametersData.list?has_content>
        <#return true>
    </#if>
    <#return false>
</#function>
