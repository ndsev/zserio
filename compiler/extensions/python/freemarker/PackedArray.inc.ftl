<#include "CompoundField.inc.ftl">
<#macro packed_create_context_definition fieldList>
    <#local createPackingContextBody>
        <#list fieldList as field>
            <#if field.isBuiltinType>
        context_builder.add_context(zserio.array.${field.arrayTraits.name}(<#rt>
                <#lt><#if field.arrayTraits.requiresElementBitSize>${field.bitSize.value}</#if>))
            <#elseif !field.array??>
        ${field.pythonTypeName}.create_packing_context(context_builder)
            </#if>
        </#list>
    </#local>
    @staticmethod
    def create_packing_context(context_builder: zserio.packed_array.PackingContextBuilder) -> None:
    <#if createPackingContextBody?has_content>
        ${createPackingContextBody}<#t>
    <#else>
        del context_builder
    </#if>
</#macro>

<#macro packed_init_context_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if !field.array??>
        <#if field.optional??>
${I}if self.${field.optional.indicatorName}():
    <@packed_init_context_field_inner field, indent+1/>
        <#else>
<@packed_init_context_field_inner field, indent/>
        </#if>
    </#if>
</#macro>

<#macro packed_init_context_field_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isBuiltinType>
${I}<@field_packing_context_name field/>.init(self.<@field_member_name field/>)
    <#else><#-- arrays are solved in packed_init_context_field -->
${I}self.<@field_member_name field/>.init_packing_context(context_iterator)
    </#if>
</#macro>

<#function packed_compound_needs_context_iterator fieldList>
    <#list fieldList as field>
        <#if field.isBuiltinType>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>