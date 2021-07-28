<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withWriterCode && fieldList?has_content>
#include <zserio/Traits.h>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/PreWriteAction.h>
#include <zserio/AllocatorPropagatingCopy.h>
<@type_includes types.anyHolder/>
<@type_includes types.allocator/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name}
{
public:
    using allocator_type = ${types.allocator.default};

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
    explicit ${name}(
            <#lt><@compound_field_constructor_type_list fieldList, 3/>,
            ChoiceTag tagHint = UNDEFINED_CHOICE, const allocator_type& allocator = allocator_type()) :
        <#if needs_compound_initialization(compoundConstructorsData)>
            m_isInitialized(false),
        <#elseif has_field_with_initialization(fieldList)>
            m_areChildrenInitialized(false),
        </#if>
        <#if fieldList?has_content>
            m_objectChoice(::std::forward<ZSERIO_T>(value), allocator)
        <#else>
            m_choiceTag(UNDEFINED_CHOICE)
        </#if>
    {
        <#if fieldList?has_content>
            <#list fieldList as field>
        <#if !field?is_first>else </#if>if (<#rt>
                <#lt>m_objectChoice.isType<<@field_cpp_type_name field/>>() && <#rt>
                <#lt>(tagHint == UNDEFINED_CHOICE || tagHint == <@choice_tag_name field/>))
                m_choiceTag = <@choice_tag_name field/>;
            </#list>
        else
            throw ::zserio::CppRuntimeException("No match in union Union!");
        </#if>
    }
    </#if>

</#if>
    <@compound_read_constructor_declaration compoundConstructorsData/>

    ~${name}() = default;
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>

    <@compound_copy_constructor_declaration compoundConstructorsData/>
    <@compound_assignment_operator_declaration compoundConstructorsData/>

    <@compound_move_constructor_declaration compoundConstructorsData/>
    <@compound_move_assignment_operator_declaration compoundConstructorsData/>
<#else>

    ${name}(const ${name}&) = default;
    ${name}& operator=(const ${name}&) = default;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = default;
</#if>

    <@compound_allocator_propagating_copy_constructor_declaration compoundConstructorsData/>
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
    uint32_t hashCode() const;
<#if withWriterCode>

    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS);
</#if>

private:
    <@inner_classes_declaration fieldList/>
<#if fieldList?has_content>
    ChoiceTag readChoiceTag(::zserio::BitStreamReader& in);
    ${types.anyHolder.name} readObject(::zserio::BitStreamReader& in, const allocator_type& allocator);
    ${types.anyHolder.name} copyObject(const allocator_type& allocator) const;

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
    ChoiceTag m_choiceTag;
<#if fieldList?has_content>
    ${types.anyHolder.name} m_objectChoice;
</#if>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
