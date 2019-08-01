<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "GrpcSerializationTraits.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
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
<#if withWriterCode>
    <@compound_fields_constructor compoundConstructorsData/>
</#if>
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

    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>
    <@compound_field_accessors_declaration field/>
    <#if field.optional??>
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

    void read(zserio::BitStreamReader& in);
<#if withWriterCode>
    void write(zserio::BitStreamWriter& out,
            zserio::PreWriteAction preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS);
</#if>

private:
    <@declare_inner_classes fieldList/>
<#if has_field_with_constraint(fieldList)>
    void checkConstraints();
</#if>
<#if withRangeCheckCode && has_field_with_range_check(fieldList)>
    void checkRanges();
</#if>
<#if has_field_with_constraint(fieldList) || (withRangeCheckCode && has_field_with_range_check(fieldList))>

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#list fieldList as field>
    <#if field.optional??>${field.optional.cppTypeName}<#else>${field.cppTypeName}</#if> <#rt>
            <#lt><@field_member_name field.name/>;
</#list>
};

<@namespace_end package.path/>
<@grpc_serialization_traits needsRpcTraits fullName/>

<@include_guard_end package.path, name/>
