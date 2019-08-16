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
<@system_includes cppSystemIncludes, false/>

#include "<@include_path package.path, "${name}.h"/>"
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

<@define_inner_classes fieldList/>
<#-- TODO empty ctor must be only if withWriterCode if reader ctor won't need empty ctors -->
<@compound_constructor_definition compoundConstructorsData/>

<@compound_read_constructor_definition compoundConstructorsData/>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

<@compound_move_constructor_definition compoundConstructorsData/>

<@compound_move_assignment_operator_definition compoundConstructorsData/>

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
    <#assign fieldOrOptional=field.optional!field>
    <#if needs_field_getter(field)>
${fieldOrOptional.cppTypeName}& ${name}::${field.getterName}()
{
    return <@field_member_name field.name/>;
}

    </#if>
${fieldOrOptional.cppArgumentTypeName} ${name}::${field.getterName}() const
{
    return <@field_member_name field.name/>;
}

    <#if needs_field_setter(field)>
void ${name}::${field.setterName}(${fieldOrOptional.cppArgumentTypeName} <@field_argument_name field.name/>)
{
    <@field_member_name field.name/> = <@field_argument_name field.name/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(${fieldOrOptional.cppTypeName}&& <@field_argument_name field.name/>)
{
    <@field_member_name field.name/> = std::move(<@field_argument_name field.name/>);
}

    </#if>
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
${I}endBitPosition = zserio::alignTo(${field.alignmentValue}, endBitPosition);
    </#if>
    <#if field.offset??>
        <#if field.offset.containsIndex>
${I}if (<@compound_get_field field/>.size() > 0)
${I}    endBitPosition = zserio::alignTo(zserio::NUM_BITS_PER_BYTE, endBitPosition);
        <#else>
${I}endBitPosition = zserio::alignTo(zserio::NUM_BITS_PER_BYTE, endBitPosition);
        </#if>
    </#if>
</#macro>
<#macro structure_bitsizeof_field field indent>
    <@structure_align_field field, indent/>
    <@compound_bitsizeof_field field, indent/>
</#macro>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <#list fieldList as field>
        <#if field.optional??>
            <#if !field.optional.clause??>
                <#-- auto optional field -->
    endBitPosition += 1;
            </#if>
    if (<@field_optional_condition field/>)
    {
        <@structure_bitsizeof_field field, 2/>
    }
        <#else>
    <@structure_bitsizeof_field field, 1/>
        </#if>
    </#list>

    return endBitPosition - bitPosition;
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
${I}    const ${field.offset.typeName} value = (${field.offset.typeName})zserio::bitsToBytes(endBitPosition);
${I}    ${field.offset.setter};
${I}}
    </#if>
    <@compound_initialize_offsets_field field, indent/>
</#macro>
size_t ${name}::initializeOffsets(size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

        <#list fieldList as field>
            <#if field.optional??>
                <#if !field.optional.clause??>
                    <#-- auto optional field -->
    endBitPosition += 1;
                </#if>
    if (<@field_optional_condition field/>)
    {
        <@structure_initialize_offsets_field field, 2/>
    }
            <#else>
    <@structure_initialize_offsets_field field, 1/>
            </#if>
        </#list>

    return endBitPosition;
    <#else>
    return bitPosition;
    </#if>
}
</#if>

bool ${name}::operator==(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> other</#if>) const
{
<#if compoundParametersData.list?has_content || fieldList?has_content>
    if (this != &other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, fieldList?has_content/>
    <#list fieldList as field>
        <#if field.optional?? && field.optional.clause??>
                (!(${field.optional.clause}) || <@field_member_name field.name/> == other.<@field_member_name field.name/>)<#if field?has_next> &&<#else>;</#if>
        <#else>
                (<@field_member_name field.name/> == other.<@field_member_name field.name/>)<#if field?has_next> &&<#else>;</#if>
        </#if>
    </#list>
    }

</#if>
    return true;
}

int ${name}::hashCode() const
{
    int result = zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
<#list fieldList as field>
    <#if field.optional?? && field.optional.clause??>
    if (${field.optional.clause})
        result = zserio::calcHashCode(result, <@field_member_name field.name/>);
    <#else>
    result = zserio::calcHashCode(result, <@field_member_name field.name/>);
    </#if>
    <#if !field?has_next>

    </#if>
</#list>
    return result;
}

<#assign needsReadNewLines=false/>
<#list fieldList as field>
    <#if has_field_any_read_check_code(field, name, 2)>
        <#assign needsReadNewLines=true/>
        <#break>
    </#if>
</#list>
void ${name}::read(zserio::BitStreamReader&<#if fieldList?has_content> in</#if>)
{
<#if fieldList?has_content>
    <#list fieldList as field>
    <@compound_read_field field, name, 1/>
        <#if field?has_next && needsReadNewLines>

        </#if>
    </#list>
</#if>
}
<#if withWriterCode>

<#assign hasPreWriteAction=needsChildrenInitialization || hasFieldWithOffset/>
<#assign needsWriteNewLines=false/>
<#list fieldList as field>
    <#if has_field_any_write_check_code(field, name, 2)>
        <#assign needsWriteNewLines=true/>
        <#break>
    </#if>
</#list>
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>, <#rt>
        zserio::PreWriteAction<#if hasPreWriteAction> preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
        <#list fieldList as field>
    <@compound_write_field field, name, 1/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    </#if>
}
</#if>

<@namespace_end package.path/>
