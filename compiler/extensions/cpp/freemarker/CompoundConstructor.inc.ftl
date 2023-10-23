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
    explicit ${compoundConstructorsData.compoundName}(<#rt>
    <#if packed>
            <#lt>ZserioPackingContext& context,
            <#nt><#rt><#-- trim only newline -->
    </#if>
            ::zserio::BitStreamReader& in<#t>
    <#if needs_compound_initialization(compoundConstructorsData)>
            <#lt>,
            const ParameterExpressions& parameterExpressions<#rt>
    </#if>
            <#lt>, const allocator_type& allocator = allocator_type());
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData memberInitializationMacroName packed=false>
    <#local hasInitializers=needs_compound_initialization(compoundConstructorsData) ||
            memberInitializationMacroName != ""/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
    <#if packed>
        ${compoundConstructorsData.compoundName}::ZserioPackingContext& context, <#t>
    </#if>
        ::zserio::BitStreamReader&<#if compoundConstructorsData.fieldList?has_content> in</#if><#t>
    <#if needs_compound_initialization(compoundConstructorsData)>
        <#lt>,
        const ParameterExpressions& parameterExpressions<#rt>
    </#if>
        , const allocator_type&<#if read_constructor_needs_allocator(compoundConstructorsData.fieldList)> allocator</#if><#t>
    <#if hasInitializers>
        <#lt>) :
    <#else>
        <#lt>)
    </#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_parameterExpressions(parameterExpressions)<#if memberInitializationMacroName != "">,</#if>
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
    <#local hasInitializers=needs_compound_initialization(compoundConstructorsData) ||
            compoundConstructorsData.fieldList?has_content/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>const ${compoundConstructorsData.compoundName}& other)<#if hasInitializers> :</#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_parameterExpressions(other.m_parameterExpressions)<#if compoundConstructorsData.fieldList?has_content>,</#if>
    </#if>
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
    <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        <#if needs_compound_initialization(compoundConstructorsData)>
    if (isInitialized())
        initializeChildren();
        <#else>
    initializeChildren();
        </#if>
    </#if>
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
    <#if needs_compound_initialization(compoundConstructorsData)>
    m_parameterExpressions = other.m_parameterExpressions;
    </#if>
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
    m_numExtendedFields = other.m_numExtendedFields;
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        <#if needs_compound_initialization(compoundConstructorsData)>
    if (isInitialized())
        initializeChildren();
        <#else>
    initializeChildren();
        </#if>
    </#if>

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
    <#local hasInitializers=needs_compound_initialization(compoundConstructorsData) ||
            compoundConstructorsData.fieldList?has_content/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>${compoundConstructorsData.compoundName}&& other)<#if hasInitializers> :</#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_parameterExpressions(other.m_parameterExpressions)<#if compoundConstructorsData.fieldList?has_content>,</#if>
    </#if>
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
    <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        <#if needs_compound_initialization(compoundConstructorsData)>
    if (isInitialized())
        initializeChildren();
        <#else>
    initializeChildren();
        </#if>
    </#if>
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
    <#if needs_compound_initialization(compoundConstructorsData)>
    m_parameterExpressions = other.m_parameterExpressions;
    </#if>
    <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
    m_numExtendedFields = other.m_numExtendedFields;
    </#if>
    <#list compoundConstructorsData.fieldList as field>
        <@compound_move_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        <#if needs_compound_initialization(compoundConstructorsData)>
    if (isInitialized())
        initializeChildren();
        <#else>
    initializeChildren();
        </#if>
    </#if>

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
    <#local hasInitializers=needs_compound_initialization(compoundConstructorsData) ||
            compoundConstructorsData.fieldList?has_content/>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>::zserio::PropagateAllocatorT,
        const ${compoundConstructorsData.compoundName}&<#rt>
        <#lt><#if hasInitializers> other</#if>,<#rt>
        <#lt> const allocator_type&<#if compoundConstructorsData.fieldList?has_content> allocator</#if>)<#rt>
        <#lt><#if hasInitializers> :</#if>
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_parameterExpressions(other.m_parameterExpressions)<#if compoundConstructorsData.fieldList?has_content>,</#if>
    </#if>
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
    <#if has_field_with_initialization(compoundConstructorsData.fieldList)>
        <#if needs_compound_initialization(compoundConstructorsData)>
    if (isInitialized())
        initializeChildren();
        <#else>
    initializeChildren();
        </#if>
    </#if>
}
</#macro>

<#macro compound_initialize_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Initializes this Zserio object and all its fields.
     *
     * This method sets <#if compoundConstructorsData.compoundParametersData.list?has_content>parameters for this Zserio object and </#if><#rt>
     <#lt>all parameters for all fields recursively.
        <#if compoundConstructorsData.compoundParametersData.list?has_content>
     *
            <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
     * \param <@parameter_argument_name compoundParameter.name/> Value of the parameter \ref ${compoundParameter.getterName} "${compoundParameter.name}".
            </#list>
        </#if>
     */
    </#if>
    void initialize(const ParameterExpressions& parameterExpressions);
    <#if withCodeComments>

    /**
     * Checks if this Zserio object is initialized.
     *
     * \return True if this Zserio object is initialized, otherwise false.
     */
    </#if>
    bool isInitialized() const;
</#macro>

<#macro compound_initialize_definition compoundConstructorsData needsChildrenInitialization>
void ${compoundConstructorsData.compoundName}::initialize(const ParameterExpressions& parameterExpressions)
{
    m_parameterExpressions = parameterExpressions;
    <#if needsChildrenInitialization>
    initializeChildren();
    </#if>
}

bool ${compoundConstructorsData.compoundName}::isInitialized() const
{
    return m_parameterExpressions.isInitialized();
}
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
