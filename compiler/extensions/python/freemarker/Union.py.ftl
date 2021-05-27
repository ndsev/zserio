<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>
<#macro choice_tag_name field>
    CHOICE_${field.snakeCaseName?upper_case}<#t>
</#macro>
<#macro union_if memberActionMacroName packed=false contextNodeVarName="">
    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choice_tag == self.<@choice_tag_name field/>:
            <#if packed>
            <@compound_field_packing_context_node field, field?index + 1 , contextNodeVarName, 3/>
            </#if>
            <@.vars[memberActionMacroName] field, 3, packed/>
    </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")
</#macro>
<#assign choiceTagArrayTraits="zserio.array.VarSizeArrayTraits">

class ${name}:
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if><#rt>
<#if fieldList?has_content>
            <#lt>,
            *,
    <#list fieldList as field>
            <@field_argument_name field/>: <@field_annotation_argument_type_choice_name field, name/> = None<#rt>
        <#if field?has_next>
            <#lt>,
        </#if>
    </#list>
</#if>
            <#lt>) -> None:
        <@compound_constructor_parameter_assignments compoundParametersData/>
        self._choice_tag: int = self.UNDEFINED_CHOICE
        self._choice: typing.Any = None
<#if fieldList?has_content>
    <#list fieldList as field>
        if <@field_argument_name field/> is not None:
        <#if !field?is_first>
            if self._choice_tag != self.UNDEFINED_CHOICE:
                raise zserio.PythonRuntimeException("Calling constructor of union ${name} is ambiguous!")
        </#if>
            self._choice_tag = self.<@choice_tag_name field/>
            <@compound_setter_field field, withWriterCode, 3/>
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
            zserio_context_node: zserio.packed_array.PackingContextNode,
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read_packed(zserio_context_node, zserio_reader)

        return instance

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return (<#rt>
<#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/> and
</#if>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt>self._choice_tag == other._choice_tag and
                    self._choice == other._choice)

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calc_hashcode(result, hash(self._choice_tag))
        result = zserio.hashcode.calc_hashcode(result, hash(self._choice))

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
        self._choice_tag = self.<@choice_tag_name field/>
        <@compound_setter_field field, withWriterCode, 2/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self) -> ${function.returnPythonTypeName}:
        return ${function.resultExpression}
</#list>

    @property
    def choice_tag(self) -> int:
        return self._choice_tag

    @staticmethod
    def create_packing_context(context_builder: zserio.packed_array.PackingContextBuilder) -> None:
<#if fieldList?has_content>
        context_builder.begin_node()
        context_builder.add_leaf(${choiceTagArrayTraits})

    <#list fieldList as field>
        <@compound_create_packing_context_field field/>
    </#list>

        context_builder.end_node()
<#else>
        del context_builder
</#if>

<#macro union_init_packing_context field indent packed>
    <#local initCode><@compound_init_packing_context_field field, indent/></#local>
    <#if initCode?has_content>
${initCode}<#t>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
${I}pass
    </#if>
</#macro>
    def init_packing_context(self, context_node: zserio.packed_array.PackingContextNode) -> None:
<#if fieldList?has_content>
        context_node.children[0].context.init(self._choice_tag)
        <@union_if "union_init_packing_context", true, "context_node"/>
<#else>
        del context_node
</#if>

    def bitsizeof(self, bitposition: int = 0) -> int:
<#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += zserio.bitsizeof.bitsizeof_varsize(self._choice_tag)

        <@union_if "compound_bitsizeof_field"/>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>

    def bitsizeof_packed(self, context_node: zserio.packed_array.PackingContextNode,
                         bitposition: int = 0) -> int:
<#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += context_node.children[0].context.bitsizeof(${choiceTagArrayTraits}(),
                                                                      end_bitposition, self._choice_tag)

        <@union_if "compound_bitsizeof_field", true, "context_node"/>

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

        end_bitposition += zserio.bitsizeof.bitsizeof_varsize(self._choice_tag)

        <@union_if "compound_initialize_offsets_field"/>

        return end_bitposition
    <#else>
        return bitposition
    </#if>

    def initialize_offsets_packed(self, context_node: zserio.packed_array.PackingContextNode,
                                  bitposition: int) -> int:
    <#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += context_node.children[0].context.bitsizeof(${choiceTagArrayTraits}(),
                                                                      end_bitposition, self._choice_tag)

        <@union_if "compound_initialize_offsets_field", true, "context_node"/>

        return end_bitposition
    <#else>
        del context_node
        return bitposition
    </#if>
</#if>

<#macro union_read_field field indent packed>
    <@compound_read_field field, name, withWriterCode, indent, packed/>
</#macro>
    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
        self._choice_tag = zserio_reader.read_varsize()

        <@union_if "union_read_field"/>
<#else>
        del zserio_reader
</#if>

    def read_packed(self, zserio_context_node: zserio.packed_array.PackingContextNode,
                    zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
        self._choice_tag = zserio_context_node.children[0].context.read(${choiceTagArrayTraits}(),
                                                                        zserio_reader)

        <@union_if "union_read_field" true "zserio_context_node"/>
<#else>
        del zserio_context_node
        del zserio_reader
</#if>
<#if withWriterCode>

<#macro union_write_field field indent packed>
    <@compound_write_field field, name, indent, packed/>
</#macro>
    def write(self, zserio_writer: zserio.BitStreamWriter, *,
              zserio_call_initialize_offsets: bool = True) -> None:
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if zserio_call_initialize_offsets:
            self.initialize_offsets(zserio_writer.bitposition)
        <#else>
        del zserio_call_initialize_offsets
        </#if>

        zserio_writer.write_varsize(self._choice_tag)

        <@union_if "union_write_field"/>
    <#else>
        del zserio_writer
        del zserio_call_initialize_offsets
    </#if>

    def write_packed(self, zserio_context_node: zserio.packed_array.PackingContextNode,
                    zserio_writer: zserio.BitStreamWriter) -> None:
    <#if fieldList?has_content>
        zserio_context_node.children[0].context.write(${choiceTagArrayTraits}(),
                                                      zserio_writer, self._choice_tag)

        <@union_if "union_write_field", true, "zserio_context_node"/>
    <#else>
        del zserio_context_node
        del zserio_writer
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field, name/>
</#list>

<#list fieldList as field>
    <@choice_tag_name field/> = ${field?index}
</#list>
    <#-- don't use CHOICE_undefined to prevent clashing with generated choice tags -->
    UNDEFINED_CHOICE = -1
