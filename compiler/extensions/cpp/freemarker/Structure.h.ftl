<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withWriterCode && fieldList?size == 1>
#include <type_traits>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
<#if has_optional_field(fieldList)>
#include <zserio/OptionalHolder.h>
</#if>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<#if has_optional_recursive_field(fieldList)>
<@namespace_begin package.path/>

class ${name};
<@namespace_end package.path/>
<@namespace_begin ["zserio", "detail"]/>

template <>
struct is_optimized_in_place<${fullName}>
{
    static const bool value = false;
};
<@namespace_end ["zserio", "detail"]/>
</#if>
<@namespace_begin package.path/>

class ${name}
{
public:
<#if withWriterCode>
    <@compound_constructor_declaration compoundConstructorsData/>
    <#if fieldList?has_content>

    <@compound_fields_constructor_template compoundConstructorsData/>

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
<#if needs_compound_initialization(compoundConstructorsData) || needsChildrenInitialization>

    <#if needs_compound_initialization(compoundConstructorsData)>
    <@compound_initialize_declaration compoundConstructorsData/>
    </#if>
    <#if needsChildrenInitialization>
    <@compound_initialize_children_declaration/>
    </#if>
</#if>

    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>
    <@compound_field_accessors_declaration field/>
    <#if field.optional??>
        <#if withWriterCode>
    void ${field.optional.resetterName}();
        </#if>
    bool ${field.optional.indicatorName}() const;
    </#if>

</#list>
    <@compound_functions_declaration compoundFunctionsData/>
    size_t bitSizeOf(size_t bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t bitPosition);
</#if>

    bool operator==(const ${name}& other) const;
    int hashCode() const;

    void read(::zserio::BitStreamReader& in);
<#if withWriterCode>
    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS);
</#if>

private:
    <@inner_classes_declaration fieldList/>
<#list fieldList as field>
    <@field_type_name field/> ${field.readerName}(::zserio::BitStreamReader& in);
    <#if !field?has_next>

    </#if>
</#list>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#list fieldList as field>
    <@field_type_name field/> <@field_member_name field/>;
</#list>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
