<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "GrpcSerializationTraits.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withWriterCode>
#include <type_traits>

</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AnyHolder.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/PreWriteAction.h>
<@system_includes headerSystemIncludes, false/>

<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

class ${name}
{
public:
    enum ChoiceTag : int32_t
    {
<#list fieldList as field>
        <@choice_tag_name field/> = ${field?index},
</#list>
        UNDEFINED_CHOICE = -1
    };

<#if withWriterCode>
    <@compound_constructor_declaration compoundConstructorsData/>
    <#if fieldList?has_content>

    <@compound_field_constructor_template_arg_list name, fieldList/>
    explicit ${name}(<#rt>
        <#lt><@compound_fields_constructor_argument_type_list compoundConstructorsData, 3/><#rt>
        <#lt><#if fieldList?has_content>, ChoiceTag tagHint = UNDEFINED_CHOICE</#if>) :
        <#if needs_compound_initialization(compoundConstructorsData)>
            <@compound_parameter_constructor_initializers compoundConstructorsData.compoundParametersData, 3, true/>
            m_isInitialized(true),
        </#if>
        <#if fieldList?has_content>
            <#list fieldList as field>
            <@compound_field_constructor_initializer_field field, field?has_next, 3/>
                <#if field.usesAnyHolder>
                    <#break>
                </#if>
            </#list>
        <#else>
            m_choiceTag(UNDEFINED_CHOICE)
        </#if>
    {
        <#if fieldList?has_content>
            <#list fieldList as field>
        <#if !field?is_first>else </#if>if (<#rt>
                <#lt>m_objectChoice.isType<${field.cppTypeName}>() && <#rt>
                <#lt>(tagHint == UNDEFINED_CHOICE || tagHint == <@choice_tag_name field/>))
                m_choiceTag = <@choice_tag_name field/>;
            </#list>
        else
            throw zserio::CppRuntimeException("No match in union Union!");
            <#if has_field_with_initialization(fieldList)>

        initializeChildren();
            </#if>
        </#if>
    }
    </#if>

</#if>
    <@compound_read_constructor_declaration compoundConstructorsData/>
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>

    <@compound_copy_constructor_declaration compoundConstructorsData/>
    <@compound_assignment_operator_declaration compoundConstructorsData/>

    <@compound_move_constructor_declaration compoundConstructorsData/>
    <@compound_move_assignment_operator_declaration compoundConstructorsData/>
</#if>
<#if needs_compound_initialization(compoundConstructorsData) || needsChildrenInitialization>

    <#if needs_compound_initialization(compoundConstructorsData)>
    <@compound_initialize_declaration compoundConstructorsData/>
    </#if>
    <#if needsChildrenInitialization>
    <@compound_initialize_children_declaration/>
    </#if>
</#if>

    ChoiceTag choiceTag() const;

    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>
    <@compound_field_accessors_declaration field/>

</#list>
    <@compound_functions_declaration compoundFunctionsData/>
    size_t bitSizeOf(size_t bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t bitPosition);
</#if>

    bool operator==(const ${name}& other) const;
    int hashCode() const;

    void read(zserio::BitStreamReader& in);
<#if withWriterCode>
    void write(zserio::BitStreamWriter& out,
            zserio::PreWriteAction preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS);
</#if>

private:
    <@declare_inner_classes fieldList/>
<#if withRangeCheckCode && has_field_with_range_check(fieldList)>
    void checkRanges();

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
    ChoiceTag m_choiceTag;
    zserio::AnyHolder m_objectChoice;
};

<@namespace_end package.path/>
<@grpc_serialization_traits needsRpcTraits fullName/>

<@include_guard_end package.path, name/>
