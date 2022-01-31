<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withWriterCode && fieldList?has_content>
#include <zserio/Traits.h>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
#include <zserio/AllocatorPropagatingCopy.h>
<#if withTypeInfoCode>
#include <zserio/ITypeInfo.h>
    <#if withReflectionCode>
<@type_includes types.reflectablePtr/>
    </#if>
</#if>
<@type_includes types.anyHolder/>
<@type_includes types.allocator/>
<@type_includes types.packingContextNode/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name}
{
<@top_private_section_declarations name, fieldList/>
public:
    using allocator_type = ${types.allocator.default};

<#if withWriterCode>
    <@compound_constructor_declaration compoundConstructorsData/>

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

    static const ::zserio::ITypeInfo& typeInfo();
    <#if withReflectionCode>
    ${types.reflectablePtr.name} reflectable(const allocator_type& allocator = allocator_type());
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

</#list>
    <@compound_functions_declaration compoundFunctionsData/>
    static void createPackingContext(${types.packingContextNode.name}& contextNode);
    void initPackingContext(${types.packingContextNode.name}& contextNode) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(${types.packingContextNode.name}& contextNode, size_t bitPosition) const;
<#if withWriterCode>

    size_t initializeOffsets(size_t bitPosition);
    size_t initializeOffsets(${types.packingContextNode.name}& contextNode, size_t bitPosition);
</#if>

    bool operator==(const ${name}& other) const;
    uint32_t hashCode() const;
<#if withWriterCode>

    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS);
    void write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out);
</#if>

private:
<#if fieldList?has_content>
    ${types.anyHolder.name} readObject(::zserio::BitStreamReader& in, const allocator_type& allocator);
    ${types.anyHolder.name} readObject(${types.packingContextNode.name}& contextNode,
            ::zserio::BitStreamReader& in, const allocator_type& allocator);
    ${types.anyHolder.name} copyObject(const allocator_type& allocator) const;

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#if fieldList?has_content>
    ${types.anyHolder.name} m_objectChoice;
</#if>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
