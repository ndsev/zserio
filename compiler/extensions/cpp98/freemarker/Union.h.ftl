<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AnyHolder.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
#include <zserio/PreWriteAction.h>
<#if withInspectorCode>
#include <zserio/inspector/BlobInspectorTree.h>
</#if>
<@system_includes headerSystemIncludes, false/>

<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

class ${name}
{
public:
<#if withWriterCode>
    <@compound_constructor_declaration compoundConstructorsData/>
</#if>
    <@compound_read_constructor_declaration compoundConstructorsData/>
<#if withInspectorCode>
    <@compound_read_tree_constructor_declaration compoundConstructorsData/>
</#if>
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>

    <@compound_copy_constructor_declaration compoundConstructorsData/>
    <@compound_assignment_operator_declaration compoundConstructorsData/>
</#if>
<#if needs_compound_initialization(compoundConstructorsData) || needsChildrenInitialization>

    <#if needs_compound_initialization(compoundConstructorsData)>
    <@compound_initialize_declaration compoundConstructorsData/>
    </#if>
    <#if needsChildrenInitialization>
    <@compound_initialize_children_declaration/>
    </#if>
</#if>

    enum ChoiceTag
    {
<#list fieldList as field>
        <@choice_tag_name field/> = ${field_index},
</#list>
        CHOICE_UNDEFINED = -1
    };

    ChoiceTag choiceTag() const;

    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>
    <@compound_field_accessors_declaration field/>

</#list>
    <@compound_functions_declaration compoundFunctionsData/>
    size_t bitSizeOf(size_t _bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t _bitPosition);
</#if>

    bool operator==(const ${name}& other) const;
    int hashCode() const;

    void read(zserio::BitStreamReader& _in);
<#if withInspectorCode>
    void read(const zserio::BlobInspectorTree& _tree);
</#if>
<#if withWriterCode>
    void write(zserio::BitStreamWriter& _out,
            zserio::PreWriteAction _preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS);
</#if>
<#if withInspectorCode>
    void write(zserio::BitStreamWriter& _out, zserio::BlobInspectorTree& _tree,
            zserio::PreWriteAction _preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS);
</#if>

private:
<#if withRangeCheckCode && has_field_with_range_check(fieldList)>
    void checkRanges();

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
    ChoiceTag m_choiceTag;
    zserio::AnyHolder m_objectChoice;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
