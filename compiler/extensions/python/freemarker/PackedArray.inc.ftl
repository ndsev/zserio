<#include "CompoundField.inc.ftl">
<#macro create_packed_context_definition fieldList>
    <#local createPackedContextBody>
        <#list fieldList as field>
            <#if field.isBuiltinType>
        context_builder.add_context(zserio.array.${field.arrayTraits.name}(<#rt>
                <#lt><#if field.arrayTraits.requiresElementBitSize>${field.bitSize.value}</#if>))
            <#elseif !field.array??>
        ${field.pythonTypeName}.create_packed_context(context_builder)
            </#if>
        </#list>
    </#local>
    @staticmethod
    def create_packed_context(context_builder: zserio.packed_array.PackingContextBuilder):
    <#if createPackedContextBody?has_content>
        ${createPackedContextBody}<#t>
    <#else>
        del context_builder
    </#if>
</#macro>

<#macro init_packed_context_definition fieldList>
    <#local initPackedContextBody>
        <#list fieldList as field>
            <#if field.optional??>
        if self.${field.optional.indicatorName}():
            <@init_packed_context_inner field, 3/>
            <#else>
        <@init_packed_context_inner field, 2/>
            </#if>
        </#list>
    </#local>
    def init_packed_context(self, context_iterator: zserio.packed_array.PackingContextIterator):
    <#if initPackedContextBody?has_content>
        ${initPackedContextBody}<#t>
    <#else>
        del context_iterator
    </#if>
</#macro>

<#macro init_packed_context_inner field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.isBuiltinType>
${I}context = next(context_iterator)
${I}context.init(self.<@field_member_name field/>)
    <#elseif !field.array??>
${I}<@field_member_name field/>.init_packed_context(context_iterator)
    </#if>
</#macro>
