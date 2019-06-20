<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#if withInspectorCode>
    <#include "Inspector.inc.ftl">
</#if>
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>
#include <zserio/VarUInt64Util.h>
<#if has_field_with_constraint(fieldList)>
#include <zserio/ConstraintException.h>
</#if>
<#if withInspectorCode>
#include <zserio/inspector/BlobInspectorTreeUtil.h>
</#if>
<@system_includes cppSystemIncludes, false/>

#include "<@include_path package.path, "${name}.h"/>"
<#if withInspectorCode>
#include "<@include_path rootPackage.path, "InspectorZserioTypeNames.h"/>"
#include "<@include_path rootPackage.path, "InspectorZserioNames.h"/>"
</#if>
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

<#assign hasAnonymousNamespace=false/>
<#list fieldList as field>
    <#if field.array?? && field.array.requiresElementFactory>
        <#if !hasAnonymousNamespace>
            <#assign hasAnonymousNamespace=true/>
<@anonymous_namespace_begin/>

        </#if>
<@define_element_factory name, field/>

        <#if field.array.elementCompound??>
            <#if needs_compound_field_initialization(field.array.elementCompound)>
<@define_element_initializer name, field/>

            <#elseif field.array.elementCompound.needsChildrenInitialization>
<@define_element_children_initializer name, field/>

            </#if>
        </#if>
    </#if>
</#list>
<#if hasAnonymousNamespace>
<@anonymous_namespace_end/>

</#if>
<#if withWriterCode>
${name}::${name}() :
    <#assign constructorMembersInitialization><@compound_constructor_members_initialization compoundConstructorsData/></#assign>
    <#if constructorMembersInitialization?has_content>
        ${constructorMembersInitialization}, m_choiceTag(CHOICE_UNDEFINED)
    <#else>
        m_choiceTag(CHOICE_UNDEFINED)
    </#if>
{
}

</#if>
<@compound_read_constructor_definition compoundConstructorsData/>

<#if withInspectorCode>
<@compound_read_tree_constructor_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
${name}::${name}(<#rt>
    <#lt>const ${name}& _other) :
        m_choiceTag(_other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_copy_constructor_initializer_field field, field_has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
    <@compound_copy_initialization compoundConstructorsData/>
}

${name}& ${name}::operator=(const ${name}& _other)
{
    m_choiceTag = _other.m_choiceTag;
    <#list fieldList as field>
    <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
    <@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#if fieldList?has_content>
    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_initialize_children_field field, name, 2, true/>
        break;
        </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
    </#if>
    <@compound_initialize_children_epilog_definition compoundConstructorsData/>
}

</#if>
${name}::ChoiceTag ${name}::choiceTag() const
{
    return m_choiceTag;
}

<@compound_parameter_accessors_definition name, compoundParametersData/>
<#macro union_set_field field>
    m_choiceTag = <@choice_tag_name field/>;
    <@compound_set_field field/>
</#macro>
<#list fieldList as field>
<@compound_field_getter_definition field name "compound_return_field"/>
<@compound_field_const_getter_definition field name "compound_return_field"/>
<@compound_field_setter_definition field name "union_set_field"/>

</#list>
<@compound_functions_definition name, compoundFunctionsData/>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> _bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

    _endBitPosition += zserio::getBitSizeOfVarUInt64(m_choiceTag);

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_bitsizeof_field field, 2/>
        break;
    </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }

    return _endBitPosition - _bitPosition;
<#else>
    return 0;
</#if>
}

bool ${name}::operator==(const ${name}& _other) const
{
    if (this != &_other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, true/>
                (m_choiceTag == _other.m_choiceTag) &&
                (m_objectChoice == _other.m_objectChoice);
    }

    return true;
}

int ${name}::hashCode() const
{
    int _result = zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
    _result = zserio::calcHashCode(_result, static_cast<int>(m_choiceTag));
    _result = zserio::calcHashCode(_result, m_objectChoice);

    return _result;
}

void ${name}::read(zserio::BitStreamReader&<#if fieldList?has_content> _in</#if>)
{
<#if fieldList?has_content>
    m_choiceTag = static_cast<ChoiceTag>(zserio::convertVarUInt64ToInt(_in.readVarUInt64()));

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_read_field field, name, 2/>
        <@compound_check_constraint_field field, name, 2/>
        break;
    </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
<#else>
    m_choiceTag = CHOICE_UNDEFINED;
</#if>
}
<#if withInspectorCode>

void ${name}::read(const zserio::BlobInspectorTree&<#if fieldList?has_content> _tree</#if>)
{
<#if fieldList?has_content>
    const size_t _treeFieldIndex = 0;
    const zserio::StringHolder& _zserioName = zserio::getBlobInspectorNode(_tree, _treeFieldIndex).getZserioName();
    <#list fieldList as field>
    <#-- compare const char* which are unique in generated code -->
    <#if field_index != 0>else </#if>if (_zserioName.get() == ${rootPackage.name}::InspectorZserioNames::<@inspector_zserio_name field.name/>.get())
    {
        m_choiceTag = <@choice_tag_name field/>;
        <@compound_read_tree_field field, false, name, 2/>
        <@compound_check_constraint_field field, name, 2/>
    }
    </#list>
    else
    {
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
<#else>
    m_choiceTag = CHOICE_UNDEFINED;
</#if>
}
</#if>
<#assign needsRangeCheck=withRangeCheckCode && has_field_with_range_check(fieldList)/>
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t _bitPosition)
{
    <#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

    _endBitPosition += zserio::getBitSizeOfVarUInt64(m_choiceTag);

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_initialize_offsets_field field, 2/>
        break;
        </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }

    return _endBitPosition;
    <#else>
    return _bitPosition;
    </#if>
}

<#assign hasPreWriteAction=needsRangeCheck || needsChildrenInitialization || hasFieldWithOffset/>
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> _out</#if>, <#rt>
        zserio::PreWriteAction<#if hasPreWriteAction> _preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    _out.writeVarUInt64(m_choiceTag);

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_check_constraint_field field, name, 2/>
        <@compound_write_field field, name, 2/>
        break;
        </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
    </#if>
}
</#if>
<#if withInspectorCode>

void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> _out</#if>, <#rt>
        zserio::BlobInspectorTree&<#if fieldList?has_content || compoundFunctionsData.list?has_content> _tree</#if>,<#lt>
        zserio::PreWriteAction<#if hasPreWriteAction> _preWriteAction</#if>)
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    <#-- don't write m_choiceTag to the tree, but move bitstream writer -->
    _out.writeVarUInt64(m_choiceTag);

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        {
            <@compound_write_tree_field field, name, rootPackage.name, 3/>
        }
        break;
        </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
    </#if>
    <@compound_functions_write_tree rootPackage.name, compoundFunctionsData/>
}
</#if>
<#if needsRangeCheck>

void ${name}::checkRanges()
{
    <#if fieldList?has_content>
    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
            <#if needs_field_range_check(field)>
        {
            <@compound_check_range_field field, name, 3, true/>
        }
            </#if>
        break;
        </#list>
    default:
        throw zserio::CppRuntimeException("No match in union ${name}!");
    }
    </#if>
}
</#if>

<@namespace_end package.path/>
