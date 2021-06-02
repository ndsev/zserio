<#include "FileHeader.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
<#if constructorAnnotatedParamList?has_content || fieldList?has_content>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if><#rt>
    <#list fieldList as field>
            <#lt>,
            <#nt><@field_argument_name field/>: <@field_annotation_argument_type_name field, name/> = <#rt>
        <#if field.initializer??>
            ${field.initializer}<#t>
        <#elseif field.optional??>
            None<#t>
        <#else>
            <#if field.isBuiltinType>
            ${field.pythonTypeName}()<#t>
            <#else>
            None<#t>
            </#if>
        </#if>
    </#list>
            <#lt>) -> None:
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#list fieldList as field>
        <@compound_setter_field field, withWriterCode, 2/>
    </#list>

</#if>
<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    @classmethod
    def from_reader(
            cls: typing.Type['${name}'],
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read(zserio_reader)

        return instance

    @classmethod
    def from_reader_packed(
            cls: typing.Type['${name}'],
            zserio_context_node: zserio.array.PackingContextNode,
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read_packed(zserio_context_node, zserio_reader)

        return instance

<#macro structure_compare_fields fieldList indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list fieldList as field>
<@compound_compare_field field/><#rt>
        <#if field?has_next>
 and
${I}<#rt>
        </#if>
    </#list>
</#macro>
    def __eq__(self, other: object) -> bool:
<#if compoundParametersData.list?has_content || fieldList?has_content>
    <#assign eqHasParenthesis = (compoundParametersData.list?size + fieldList?size) gt 1/>
        if isinstance(other, ${name}):
            return <#if eqHasParenthesis>(</#if><#rt>
    <#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/><#if fieldList?has_content> and<#elseif eqHasParenthesis>)</#if>
    </#if>
    <#if fieldList?has_content>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt><@structure_compare_fields fieldList, 5/><#if eqHasParenthesis>)</#if>
    </#if>

        return False
<#else>
        return isinstance(other, ${name})
</#if>

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
<#list fieldList as field>
        <@compound_hashcode_field field/>
</#list>

        return result
<#list compoundParametersData.list as parameter>

    @property
    def ${parameter.propertyName}(self) -> ${parameter.pythonTypeName}:
        <@compound_parameter_accessor parameter/>
</#list>
<#list fieldList as field>

    @property
    def ${field.propertyName}(self) -> <@field_annotation_argument_type_name field, name/>:
        <@compound_getter_field field/>
    <#if withWriterCode>

    @${field.propertyName}.setter
    def ${field.propertyName}(self, <#rt>
            <#lt><@field_argument_name field/>: <@field_annotation_argument_type_name field, name/>) -> None:
        <@compound_setter_field field, withWriterCode, 2/>
    </#if>
    <#if field.optional??>

    def ${field.optional.indicatorName}(self) -> bool:
        return <#if field.optional.clause??>${field.optional.clause}<#else>not self.<@field_member_name field/> is None</#if>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self) -> ${function.returnPythonTypeName}:
        return ${function.resultExpression}
</#list>

    @staticmethod
    def create_packing_context(context_builder: zserio.array.PackingContextBuilder) -> None:
    <#if fieldList?has_content>
        context_builder.begin_node()
        <#list fieldList as field>
        <@compound_create_packing_context_field field/>
        </#list>
        context_builder.end_node()
    <#else>
        del context_builder
    </#if>

    def init_packing_context(self, context_node: zserio.array.PackingContextNode) -> None:
<#if compound_needs_packing_context_node(fieldList)>
    <#list fieldList as field>
        <@compound_field_packing_context_node field, field?index, "context_node", 2/>
        <@compound_init_packing_context_field field, 2/>
    </#list>
<#else>
        del context_node
</#if>

    def bitsizeof(self, bitposition: int = 0) -> int:
<#if fieldList?has_content>
        end_bitposition = bitposition
    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>

    def bitsizeof_packed(self, context_node: zserio.array.PackingContextNode,
                         bitposition: int = 0) -> int:
<#if fieldList?has_content>
    <#if !compound_needs_packing_context_node(fieldList)>
        del context_node

    </#if>
        end_bitposition = bitposition
    <#list fieldList as field>
        <@compound_field_packing_context_node field, field?index, "context_node", 2/>
        <@compound_bitsizeof_field field, 2, true/>
    </#list>

        return end_bitposition - bitposition
<#else>
        del context_node
        del bitposition

        return 0
</#if>
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
    <#if fieldList?has_content>
        end_bitposition = bitposition
        <#list fieldList as field>
            <@compound_initialize_offsets_field field, 2/>
        </#list>

        return end_bitposition
    <#else>
        return bitposition
    </#if>

    def initialize_offsets_packed(self, context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
    <#if fieldList?has_content>
        <#if !compound_needs_packing_context_node(fieldList)>
        del context_node

        </#if>
        end_bitposition = bitposition
        <#list fieldList as field>
        <@compound_field_packing_context_node field, field?index, "context_node", 2/>
        <@compound_initialize_offsets_field field, 2, true/>
        </#list>

        return end_bitposition
    <#else>
        del context_node
        return bitposition
    </#if>
</#if>

<#assign needsReadNewLines=false/>
<#list fieldList as field>
    <#if has_field_any_read_check_code(field, name, 2)>
        <#assign needsReadNewLines=true/>
        <#break>
    </#if>
</#list>
    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
    <#list fieldList as field>
        <@compound_read_field field, name, withWriterCode, 2/>
        <#if field?has_next && needsReadNewLines>

        </#if>
    </#list>
<#else>
        del zserio_reader
</#if>

    def read_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                    zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
    <#if !compound_needs_packing_context_node(fieldList)>
        del zserio_context_node

    </#if>
    <#list fieldList as field>
        <@compound_field_packing_context_node field, field?index, "zserio_context_node", 2/>
        <@compound_read_field field, name, withWriterCode, 2, true/>
        <#if field?has_next>

        </#if>
    </#list>
<#else>
        del zserio_context_node
        del zserio_reader
</#if>
<#if withWriterCode>

    <#assign needsWriteNewLines=false/>
    <#list fieldList as field>
        <#if has_field_any_write_check_code(field, name, 2)>
            <#assign needsWriteNewLines=true/>
            <#break>
        </#if>
    </#list>
    def write(self, zserio_writer: zserio.BitStreamWriter, *,
              zserio_call_initialize_offsets: bool = True) -> None:
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if zserio_call_initialize_offsets:
            self.initialize_offsets(zserio_writer.bitposition)
        <#else>
        del zserio_call_initialize_offsets
        </#if>

        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    <#else>
        del zserio_writer
        del zserio_call_initialize_offsets
    </#if>

    def write_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                     zserio_writer: zserio.BitStreamWriter) -> None:
    <#if fieldList?has_content>
        <#if !compound_needs_packing_context_node(fieldList)>
        del zserio_context_node

        </#if>
        <#list fieldList as field>
        <@compound_field_packing_context_node field, field?index, "zserio_context_node", 2/>
        <@compound_write_field field, name, 2, true/>
            <#if field?has_next>

            </#if>
        </#list>
    <#else>
        del zserio_context_node
        del zserio_writer
    </#if>
</#if>
<#list fieldList as field>
    <@define_offset_checker field, name/>
    <#if withWriterCode>
        <@define_offset_setter field/>
    </#if>
    <@define_element_creator field, name/>
</#list>
