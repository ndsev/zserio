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
    <#local hasInitializers=needs_compound_initialization(compoundConstructorsData) ||
            has_field_with_initialization(compoundConstructorsData.fieldList) ||
            memberInitializationMacroName != ""/>
    <#local numExtendedFields=num_extended_fields(compoundConstructorsData.fieldList)/>
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
        <#if (numExtendedFields > 0)>
        m_numExtendedFields(${numExtendedFields}),
        </#if>
        <@.vars[memberInitializationMacroName]/>
    </#if>
{
}
</#macro>

<#macro compound_noinit_constructor_declaration compoundConstructorsData>
<#if withCodeComments>
    /**
     * NoInit constructor to be called from read operation.
     *
     * \param allocator Allocator to construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(::zserio::NoInitT, const allocator_type& allocator) noexcept;
</#macro>

<#macro compound_noinit_constructor_definition compoundConstructorsData initializers=noInitInitializers()>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}<#rt>
(::zserio::NoInitT, const allocator_type& allocator) noexcept<#if initializers?has_content> :</#if>
<#list initializers as init>
        ${init}<#if init?has_next>,</#if>
</#list>
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
    <#if parameterArgs(true)?has_content>
            <#lt>,
            ${parameterArgs(true)}<#rt>
    </#if>
            <#lt>, const allocator_type& allocator = allocator_type());
</#macro>

<#macro compound_read_constructor_definition compoundConstructorsData packed=false>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
<#if packed>${compoundConstructorsData.compoundName}::ZserioPackingContext& context, </#if><#rt>
::zserio::BitStreamReader& in, <#rt>
<#if parameterArgs(true)?has_content>${parameterArgs(true)}, </#if><#rt>
const allocator_type& allocator<#rt>
) :
        ${compoundConstructorsData.compoundName}(::zserio::NoInit, allocator)
    <#-- #if memberInitializationMacroName != "">
        <#if (num_extended_fields(compoundConstructorsData.fieldList) > 0)>
        m_numExtendedFields(0),
        </#if>
        <@.vars[memberInitializationMacroName] packed/>
    </#if-->
{
    read(<#if packed>context, </#if>in<#if parameterArgs(false)?has_content>, ${parameterArgs(false)}</#if>, allocator);
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

<#macro compound_copy_constructor_no_init_declaration compoundConstructorsData>
    <#if withCodeComments>

    /**
     * Copy constructor which prevents initialization.
     *
     * Note that the object will be initialized later by a parent compound.
     *
     * \param other Instance to construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(::zserio::NoInitT,
            const ${compoundConstructorsData.compoundName}& other);
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
    <@compound_copy_initialization compoundConstructorsData/>
}
</#macro>

<#macro compound_copy_constructor_no_init_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(::zserio::NoInitT,
        const ${compoundConstructorsData.compoundName}&<#if compoundConstructorsData.fieldList?has_content> other</#if>) :
        m_isInitialized(false)<#if compoundConstructorsData.fieldList?has_content>,</#if>
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

<#macro compound_assignment_no_init_declaration compoundConstructorsData>
    <#if withCodeComments>

    /**
     * Assignment which prevents initialization.
     *
     * Note that the object will be initialized later by a parent compound.
     *
     * \param other Instance to assign from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}& assign(::zserio::NoInitT, const ${compoundConstructorsData.compoundName}& other);
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
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}
</#macro>

<#macro compound_assignment_no_init_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::assign(::zserio::NoInitT,
        const ${compoundConstructorsData.compoundName}&<#if compoundConstructorsData.fieldList?has_content> other</#if>)
{
    m_isInitialized = false;
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
     * \param other Instance to move-construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_constructor_no_init_declaration compoundConstructorsData>
    <#if withCodeComments>

    /**
     * Move constructor which prevents initialization.
     *
     * Note that the object will be initialized later by a parent compound.
     *
     * \param other Instance to move-construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(::zserio::NoInitT, ${compoundConstructorsData.compoundName}&& other);
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
    <@compound_copy_initialization compoundConstructorsData/>
}
</#macro>

<#macro compound_move_constructor_no_init_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(::zserio::NoInitT,
        ${compoundConstructorsData.compoundName}&&<#if compoundConstructorsData.fieldList?has_content> other</#if>) :
        m_isInitialized(false)<#if compoundConstructorsData.fieldList?has_content>,</#if>
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
     * \param other Instance to move-assign from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}& operator=(${compoundConstructorsData.compoundName}&& other);
</#macro>

<#macro compound_move_assignment_no_init_declaration compoundConstructorsData>
    <#if withCodeComments>

    /**
     * Move assignment which prevents initialization.
     *
     * Note that the object will be initialized later by a parent compound.
     *
     * \param other Instance to move-assign from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}& assign(::zserio::NoInitT,
            ${compoundConstructorsData.compoundName}&& other);
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
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}
</#macro>

<#macro compound_move_assignment_no_init_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}& ${compoundConstructorsData.compoundName}::assign(::zserio::NoInitT,
        ${compoundConstructorsData.compoundName}&&<#if compoundConstructorsData.fieldList?has_content> other</#if>)
{
    m_isInitialized = false;
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

<#macro compound_allocator_propagating_copy_constructor_no_init_declaration compoundConstructorsData>
    <#if withCodeComments>
    /**
     * Copy constructor with propagating allocator which prevents initialization.
     *
     * \param other Instance to construct from.
     * \param allocator Allocator to construct from.
     */
    </#if>
    ${compoundConstructorsData.compoundName}(::zserio::PropagateAllocatorT, ::zserio::NoInitT,
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
    <#if initialization?has_content>
    ${initialization}<#t>
    </#if>
}
</#macro>

<#macro compound_allocator_propagating_copy_constructor_no_init_definition compoundConstructorsData>
${compoundConstructorsData.compoundName}::${compoundConstructorsData.compoundName}(<#rt>
        <#lt>::zserio::PropagateAllocatorT, ::zserio::NoInitT,
        const ${compoundConstructorsData.compoundName}&<#rt>
        <#lt><#if compoundConstructorsData.fieldList?has_content || initialization?has_content> other</#if>,<#rt>
        <#lt> const allocator_type&<#if compoundConstructorsData.fieldList?has_content> allocator</#if>) :
        m_isInitialized(false)<#if compoundConstructorsData.fieldList?has_content>,</#if>
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
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData, 3/></#local>
    void initialize(<#rt>
    <#if constructorArgumentTypeList?has_content>

    </#if>
            <#lt>${constructorArgumentTypeList});
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
