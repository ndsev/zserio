<#include "CompoundField.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#macro compound_default_constructor compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Default constructor.
     */
     </#if>
    ${compoundConstructorsData.compoundName}() noexcept :
            ${compoundConstructorsData.compoundName}(allocator_type())
    {}
</#macro>

<#macro compound_constructor_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Default constructor from allocator.
     *
     * \param allocator Allocator to construct from.
     */
    </#if>
    explicit ${compoundConstructorsData.compoundName}(const allocator_type& allocator) noexcept;
</#macro>

<#macro compound_constructor_definition compoundConstructorsData memberInitializationMacroName="">
    <#local hasInitializers=memberInitializationMacroName != ""/>
    <#local numExtendedFields=num_extended_fields(compoundConstructorsData.fieldList)/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>const allocator_type&<#rt>
        <#lt><#if empty_constructor_needs_allocator(compoundConstructorsData.fieldList)> allocator</#if>) <#rt>
        <#lt>noexcept<#if hasInitializers> :</#if>
    <#if memberInitializationMacroName != "">
        <#if (numExtendedFields > 0)>
        m_numExtendedFields(${numExtendedFields}),
        </#if>
        <@.vars[memberInitializationMacroName]/>
    </#if>
{
}
</#macro>

<#macro compound_read_constructor_declaration compoundConstructorsData packed=false>
    <#if withCodeComments>
    /**
     * Read constructor.
     *
        <#if packed>
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
        </#if>
     * \param in Bit stream reader to use.
        <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
     * \param <@parameter_argument_name compoundParameter.name/> Value of the parameter \ref ${compoundParameter.getterName} "${compoundParameter.name}".
        </#list>
     * \param allocator Allocator to use.
     */
    </#if>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    explicit ${compoundConstructorsData.compoundName}(<#rt>
    <#if packed>
            <#lt>ZserioPackingContext& context,
            <#nt><#rt><#-- trim only newline -->
    </#if>
            ::zserio::BitStreamReader& in<#t>
    <#if constructorArgumentTypeList?has_content>
            <#lt>,
            ${constructorArgumentTypeList}<#t>
    </#if>
            <#lt>, const allocator_type& allocator = allocator_type());
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData memberInitializationMacroName packed=false>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 2/></#local>
    <#local hasInitializers=memberInitializationMacroName != ""/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
    <#if packed>
        ${compoundConstructorsData.compoundName}::ZserioPackingContext& context, <#t>
    </#if>
        ::zserio::BitStreamReader&<#if compoundConstructorsData.fieldList?has_content> in</#if><#t>
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
    <#if memberInitializationMacroName != "">
        <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
        m_numExtendedFields(0),
        </#if>
        <@.vars[memberInitializationMacroName] packed/>
    </#if>
{
}
</#macro>

<#macro compound_copy_constructor_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Copy constructor.
     *
     * \param other Instance to construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(const ${compoundConstructorsData.compoundName}& other);
</#macro>

<#macro compound_copy_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>const ${compoundConstructorsData.compoundName}& other)<#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
        m_numExtendedFields(other.m_numExtendedFields),
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_copy_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
}
</#macro>

<#macro compound_assignment_operator_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Assignment operator.
     *
     * \param other Instance to assign from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}& operator=(const ${compoundConstructorsData.compoundName}& other);
</#macro>

<#macro compound_assignment_operator_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::operator=(<#rt>
    <#lt>const ${compoundConstructorsData.compoundName}& other)
{
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
    m_numExtendedFields = other.m_numExtendedFields;
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>

    return *this;
}
</#macro>

<#macro compound_move_constructor_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Move constructor.
     *
     * \param other Instance to move from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>${compoundConstructorsData.compoundName}&& other)<#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
        m_numExtendedFields(other.m_numExtendedFields),
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_move_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
}
</#macro>

<#macro compound_move_assignment_operator_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Move assignment operator.
     *
     * \param other Instance to assign from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}& operator=(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_assignment_operator_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::operator=(<#rt>
    <#lt>${compoundConstructorsData.compoundName}&& other)
{
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
    m_numExtendedFields = other.m_numExtendedFields;
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_move_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>

    return *this;
}
</#macro>

<#macro compound_allocator_propagating_copy_constructor_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Copy constructor with propagating allocator.
     *
     * \param other Instance to construct from.
     * \param allocator Allocator to construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(::zserio::PropagateAllocatorT,
            const ${compoundConstructorsData.compoundName}& other, const allocator_type& allocator);
</#macro>

<#macro compound_allocator_propagating_copy_constructor_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>::zserio::PropagateAllocatorT,
        const ${compoundConstructorsData.compoundName}&<#rt>
        <#lt><#if compoundConstructorsData.fieldList?has_content || initialization?has_content> other</#if>,<#rt>
        <#lt> const allocator_type&<#if compoundConstructorsData.fieldList?has_content> allocator</#if>)<#rt>
        <#lt><#if compoundConstructorsData.fieldList?has_content> :</#if>
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
        m_numExtendedFields(other.m_numExtendedFields),
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_allocator_propagating_copy_constructor_initializer_field field, field?has_next, 2/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
{
}
</#macro>

<#macro compound_constructor_argument_type_list compoundConstructorsData indent>
    <@compound_parameter_arguments_type_list compoundConstructorsData.compoundParametersData, indent/>
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
