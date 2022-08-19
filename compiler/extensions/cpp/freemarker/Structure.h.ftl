<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withWriterCode && fieldList?has_content>
#include <zserio/Traits.h>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
    <#if withReflectionCode>
<@type_includes types.reflectablePtr/>
    </#if>
</#if>
<@type_includes types.allocator/>
<@type_includes types.packingContextNode/>
<#if has_optional_field(fieldList)>
    <#if has_optional_recursive_field(fieldList)>
<@type_includes types.heapOptionalHolder/>
    </#if>
    <#if has_optional_non_recursive_field(fieldList)>
<@type_includes types.inplaceOptionalHolder/>
    </#if>
</#if>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if docComments??>
<@doc_comments docComments/>
</#if>
class ${name}
{
<@top_private_section_declarations name, fieldList/>
public:
    using allocator_type = ${types.allocator.default};

<#if withWriterCode>
    <@compound_constructor_declaration compoundConstructorsData/>
    <#if fieldList?has_content>

    <@compound_field_constructor_template_arg_list compoundConstructorsData.compoundName,
            compoundConstructorsData.fieldList/>
    <#if compoundConstructorsData.fieldList?size == 1>explicit </#if>${compoundConstructorsData.compoundName}(
            <#lt><@compound_field_constructor_type_list compoundConstructorsData.fieldList, 3/>,
            const allocator_type& allocator = allocator_type()) :
            ${compoundConstructorsData.compoundName}(allocator)
    {
        <#list compoundConstructorsData.fieldList as field>
        <@field_member_name field/> = <#rt>
            <#if !field.typeInfo.isSimple || field.optional??>
                <#lt><@compound_setter_field_forward_value field/>;
            <#else>
                <#lt><@compound_setter_field_value field/>;
            </#if>
        </#list>
    }
    </#if>

</#if>
    <@compound_read_constructor_declaration compoundConstructorsData/>
    <@compound_read_constructor_declaration compoundConstructorsData, true/>

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
<#if withTypeInfoCode>

    static const ${types.typeInfo.name}& typeInfo();
    <#if withReflectionCode>
    ${types.reflectableConstPtr.name} reflectable(const allocator_type& allocator = allocator_type()) const;
        <#if withWriterCode>
    ${types.reflectablePtr.name} reflectable(const allocator_type& allocator = allocator_type());
        </#if>
    </#if>
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
    bool ${field.optional.isUsedIndicatorName}() const;
        <#if withWriterCode>
    bool ${field.optional.isSetIndicatorName}() const;
    void ${field.optional.resetterName}();
        </#if>
    </#if>

</#list>
    <@compound_functions_declaration compoundFunctionsData/>
    static void createPackingContext(${types.packingContextNode.name}& contextNode);
    void initPackingContext(${types.packingContextNode.name}& contextNode) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(${types.packingContextNode.name}& contextNode, size_t bitPosition) const;
<#if withWriterCode>

    size_t initializeOffsets(size_t bitPosition = 0);
    size_t initializeOffsets(${types.packingContextNode.name}& contextNode, size_t bitPosition);
</#if>

    bool operator==(const ${name}& other) const;
    uint32_t hashCode() const;
<#if withWriterCode>

    void write(::zserio::BitStreamWriter& out) const;
    void write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out) const;
</#if>

private:
<#list fieldList as field>
    <@field_member_type_name field/> ${field.readerName}(::zserio::BitStreamReader& in<#rt>
    <#if field.needsAllocator || field.holderNeedsAllocator>
            <#lt>,
            const allocator_type& allocator<#rt>
    </#if>
    <#lt>);
    <#if field.isPackable>
    <@field_member_type_name field/> ${field.readerName}(${types.packingContextNode.name}& contextNode,
            ::zserio::BitStreamReader& in<#rt>
        <#if field.needsAllocator || field.holderNeedsAllocator>
            , const allocator_type& allocator<#t>
        </#if>
            <#lt>);
    </#if>
    <#if !field?has_next>

    </#if>
</#list>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#list fieldList as field>
    <@field_member_type_name field/> <@field_member_name field/>;
</#list>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
