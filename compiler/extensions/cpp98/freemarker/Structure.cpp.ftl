<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>
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
    <#if field.array??>
        <#if field.offset?? && field.offset.containsIndex>
            <#if !hasAnonymousNamespace>
                <#assign hasAnonymousNamespace=true/>
<@anonymous_namespace_begin/>
            </#if>
<@define_offset_checker name, field/>

            <#if withWriterCode>
<@define_offset_setter name, field/>

            </#if>
        </#if>
        <#if field.array.requiresElementFactory>
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
    </#if>
</#list>
<#if hasAnonymousNamespace>
<@anonymous_namespace_end/>

</#if>
<#if withWriterCode>
<#macro compound_constructor_fields_initialization constructorMembersInitialization>
    <#if constructorMembersInitialization?has_content>
        <#local startedComma=true>
        ${constructorMembersInitialization}<#rt>
    <#else>
        <#local startedComma=false>
    </#if>
    <#list fieldList as field>
        <#-- string types are not simple types but can have default value (initializer) -->
        <#if !field.optionalHolder?? && (field.isSimpleType || field.initializer??)>
        <#if startedComma>
            <#lt>,
        </#if>
        m_${field.name}(<#if field.initializer??>${field.initializer}<#else>${field.cppTypeName}()</#if>)<#rt>
            <#local startedComma=true>
        </#if>
    </#list>
</#macro>
${name}::${name}()<#rt>
    <#assign constructorMembersInitialization><@compound_constructor_members_initialization compoundConstructorsData/></#assign>
    <#assign constructorFieldsInitialization><@compound_constructor_fields_initialization constructorMembersInitialization/></#assign>
    <#if constructorFieldsInitialization?has_content>
        <#lt> :
        <#lt>${constructorFieldsInitialization}
    <#else>

    </#if>
{
    <#list fieldList as field>
        <#if field.optionalHolder??>
            <#if !field.optional?? || field.initializer??>
            <#-- optional fields can have initializers -->
    m_${field.name}.reset(new (m_${field.name}.getResetStorage())
        ${field.cppTypeName}(<#if field.initializer??>${field.initializer}</#if>));
            </#if>
        </#if>
    </#list>
}

</#if>
<@compound_read_constructor_definition compoundConstructorsData/>

<#if withInspectorCode>
<@compound_read_tree_constructor_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#list fieldList as field>
    <@compound_initialize_children_field field, name, 1/>
    </#list>
    <@compound_initialize_children_epilog_definition compoundConstructorsData/>
}

</#if>
<@compound_parameter_accessors_definition name, compoundParametersData/>
<#list fieldList as field>
<@compound_field_getter_definition field name "compound_return_field"/>
<@compound_field_const_getter_definition field name "compound_return_field"/>
<@compound_field_setter_definition field name "compound_set_field"/>

    <#if field.optional??>
bool ${name}::${field.optional.indicatorName}() const
{
    return (<@field_optional_condition field/>);
}

    </#if>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
<#macro structure_align_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.alignmentValue??>
${I}_endBitPosition = zserio::alignTo(${field.alignmentValue}, _endBitPosition);
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
${I}if (${field.getterName}().size() > 0)
${I}    _endBitPosition = zserio::alignTo(zserio::NUM_BITS_PER_BYTE, _endBitPosition);
        <#else>
${I}_endBitPosition = zserio::alignTo(zserio::NUM_BITS_PER_BYTE, _endBitPosition);
        </#if>
    </#if>
</#macro>
<#macro structure_bitsizeof_field field indent>
    <@structure_align_field field, indent/>
    <@compound_bitsizeof_field field, indent/>
</#macro>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> _bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

    <#list fieldList as field>
        <#if field.optional??>
            <#if !field.optional.clause??>
                <#-- auto optional field -->
    _endBitPosition += 1;
            </#if>
    if (<@field_optional_condition field/>)
    {
        <@structure_bitsizeof_field field, 2/>
    }
        <#else>
    <@structure_bitsizeof_field field, 1/>
        </#if>
    </#list>

    return _endBitPosition - _bitPosition;
<#else>
    return 0;
</#if>
}
<#if withWriterCode>

<#macro structure_initialize_offsets_field field indent>
    <@structure_align_field field, indent/>
    <#if field.offset?? && !field.offset.containsIndex>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}{
${I}    const ${field.offset.typeName} _value = (${field.offset.typeName})zserio::bitsToBytes(_endBitPosition);
${I}    ${field.offset.setter};
${I}}
    </#if>
    <@compound_initialize_offsets_field field, indent/>
</#macro>
size_t ${name}::initializeOffsets(size_t _bitPosition)
{
    <#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

        <#list fieldList as field>
            <#if field.optional??>
                <#if !field.optional.clause??>
                    <#-- auto optional field -->
    _endBitPosition += 1;
                </#if>
    if (<@field_optional_condition field/>)
    {
        <@structure_initialize_offsets_field field, 2/>
    }
            <#else>
    <@structure_initialize_offsets_field field, 1/>
            </#if>
        </#list>

    return _endBitPosition;
    <#else>
    return _bitPosition;
    </#if>
}
</#if>

bool ${name}::operator==(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> _other</#if>) const
{
<#if compoundParametersData.list?has_content || fieldList?has_content>
    if (this != &_other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, fieldList?has_content/>
    <#list fieldList as field>
        <#if field.optional?? && field.optional.clause??>
                (!(${field.optional.clause}) || m_${field.name} == _other.m_${field.name})<#if field_has_next> &&<#else>;</#if>
        <#else>
                (m_${field.name} == _other.m_${field.name})<#if field_has_next> &&<#else>;</#if>
        </#if>
    </#list>
    }

</#if>
    return true;
}

int ${name}::hashCode() const
{
    int _result = zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
<#list fieldList as field>
    <#if field.optional?? && field.optional.clause??>
    if (${field.optional.clause})
        _result = zserio::calcHashCode(_result, m_${field.name});
    <#else>
    _result = zserio::calcHashCode(_result, m_${field.name});
    </#if>
    <#if !field_has_next>

    </#if>
</#list>
    return _result;
}

void ${name}::read(zserio::BitStreamReader&<#if fieldList?has_content> _in</#if>)
{
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 1/>
    </#list>
    <#if has_field_with_constraint(fieldList)>

    checkConstraints();
    </#if>
</#if>
}
<#if withInspectorCode>

void ${name}::read(const zserio::BlobInspectorTree&<#if fieldList?has_content> _tree</#if>)
{
<#if fieldList?has_content>
    size_t _treeFieldIndex = 0;
    <#list fieldList as field>
    <@compound_read_tree_field field, field_has_next, name, 1/>
        <#if field_has_next>

        </#if>
    </#list>
    <#if has_field_with_constraint(fieldList)>

    checkConstraints();
    </#if>
</#if>
}
</#if>
<#assign needsRangeCheck=withRangeCheckCode && has_field_with_range_check(fieldList)/>
<#if withWriterCode>

<#assign hasPreWriteAction=needsRangeCheck || needsChildrenInitialization || hasFieldWithOffset/>
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> _out</#if>, <#rt>
        zserio::PreWriteAction<#if hasPreWriteAction> _preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
        <#if has_field_with_constraint(fieldList)>
    checkConstraints();
        </#if>
        <#list fieldList as field>
    <@compound_write_field field, name, 1/>
        </#list>
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
        <#if has_field_with_constraint(fieldList)>
    checkConstraints();
        </#if>
        <#if fieldList?size gt 1>
    _tree.reserveChildren(${fieldList?size});
        </#if>
        <#list fieldList as field>

    <@compound_write_tree_field field, name, rootPackage.name, 1/>
        </#list>
    </#if>
    <@compound_functions_write_tree rootPackage.name, compoundFunctionsData/>
}
</#if>
<#if has_field_with_constraint(fieldList)>

void ${name}::checkConstraints()
{
    <#list fieldList as field>
    <@compound_check_constraint_field field, name, 1/>
    </#list>
}
</#if>
<#if needsRangeCheck>

void ${name}::checkRanges()
{<#rt>
    <#list fieldList as field>
        <#if needs_field_range_check(field)>

        </#if>
    <@compound_check_range_field field, name, 1/>
    </#list>
}
</#if>

<@namespace_end package.path/>
