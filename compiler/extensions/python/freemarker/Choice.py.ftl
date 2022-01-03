<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl">
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
selector == (${expressionList?first})<#rt>
    <#else>
selector in (<#list expressionList as expression>${expression}<#if expression?has_next>, </#if></#list>)<#rt>
    </#if>
</#macro>
<#macro choice_if memberActionMacroName packed=false>
        selector = ${selector}

    <#local fieldIndex=0>
    <#list caseMemberList as caseMember>
        <#if caseMember?has_next || !isDefaultUnreachable>
        <#if caseMember?is_first>if <#else>elif </#if><@choice_selector_condition caseMember.expressionList/>:
        <#else>
        else:
        </#if>
        <#if caseMember.compoundField??>
            <@.vars[memberActionMacroName] caseMember.compoundField, 3, packed, fieldIndex/>
            <#local fieldIndex+=1>
        <#else>
            pass
        </#if>
    </#list>
    <#if !isDefaultUnreachable>
        else:
        <#if defaultMember??>
            <#if defaultMember.compoundField??>
            <@.vars[memberActionMacroName] defaultMember.compoundField, 3, packed, fieldIndex/>
                <#local fieldIndex+=1>
            <#else>
            pass
            </#if>
        <#else>
            raise zserio.PythonRuntimeException("No match in choice ${name}!")
        </#if>
    </#if>
</#macro>

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
        self._choice: typing.Any = None
<#if fieldList?has_content>
    <#list fieldList as field>
        if <@field_argument_name field/> is not None:
        <#if !field?is_first>
            if self._choice != None:
                raise zserio.PythonRuntimeException("Calling constructor of choice ${name} is ambiguous!")
        </#if>
            <@compound_setter_field field, 3/>
     </#list>
</#if>

<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    @classmethod
    def from_reader(
            cls: typing.Type['${name}'],
            reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read(reader)

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
<#if withTypeInfoCode>

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
        field_list: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list fieldList as field>
            <@member_info_field field field?has_next/>
    </#list>
        ]
        case_list: typing.List[zserio.typeinfo.CaseInfo] = [
            <@cases_info caseMemberList, defaultMember!/>
        ]
    <#if compoundParametersData.list?has_content>
        parameter_list: typing.List[zserio.typeinfo.MemberInfo] = [
        <#list compoundParametersData.list as parameter>
            <@member_info_parameter parameter parameter?has_next/>
        </#list>
        ]
    </#if>
    <#if compoundFunctionsData.list?has_content>
        function_list: typing.List[zserio.typeinfo.MemberInfo] = [
        <#list compoundFunctionsData.list as function>
            <@member_info_function function function?has_next/>
        </#list>
        ]
    </#if>
        attribute_list = {
            zserio.typeinfo.TypeAttribute.FIELDS : field_list,
            zserio.typeinfo.TypeAttribute.CASES : case_list,
    <#if compoundParametersData.list?has_content>
            zserio.typeinfo.TypeAttribute.PARAMETERS : parameter_list,
    </#if>
    <#if compoundFunctionsData.list?has_content>
            zserio.typeinfo.TypeAttribute.FUNCTIONS : function_list,
    </#if>
            zserio.typeinfo.TypeAttribute.SELECTOR : '${selector}'<#if templateInstantiation??>,</#if>
    <#if templateInstantiation??>
            <@type_info_template_instantiation_attributes templateInstantiation/>

    </#if>
        }

        return zserio.typeinfo.TypeInfo("${schemaTypeName}", ${name}, attributes=attribute_list)
</#if>

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return (<@compound_compare_parameters compoundParametersData, 5/> and
                    self._choice == other._choice)

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
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
        <@compound_setter_field field, 2/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.functionName}(self) -> ${function.returnPythonTypeName}:
        return ${function.resultExpression}
</#list>

    @staticmethod
    def create_packing_context(zserio_context_node: zserio.array.PackingContextNode) -> None:
    <#if fieldList?has_content>
        <#list fieldList as field>
        <@compound_create_packing_context_field field/>
        </#list>
    <#else>
        del zserio_context_node
    </#if>

<#macro choice_init_packing_context_field field indent packed index>
    <#local initCode><@compound_init_packing_context_field field, index, indent/></#local>
    <#if initCode?has_content>
${initCode}<#rt>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
${I}pass
    </#if>
</#macro>
    def init_packing_context(self, zserio_context_node: zserio.array.PackingContextNode) -> None:
<#if compound_needs_packing_context_node(fieldList)>
        <@choice_if "choice_init_packing_context_field", true/>
<#else>
        del zserio_context_node
</#if>

    def bitsizeof(self, bitposition: int = 0) -> int:
<#if fieldList?has_content>
        end_bitposition = bitposition

        <@choice_if "compound_bitsizeof_field"/>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>

    def bitsizeof_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                         bitposition: int = 0) -> int:
<#if !compound_needs_packing_context_node(fieldList)>
        del zserio_context_node

</#if>
<#if fieldList?has_content>
        end_bitposition = bitposition

        <@choice_if "compound_bitsizeof_field", true/>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
    <#if fieldList?has_content>
        end_bitposition = bitposition

        <@choice_if "compound_initialize_offsets_field"/>

        return end_bitposition
    <#else>
        return bitposition
    </#if>

    def initialize_offsets_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
<#if !compound_needs_packing_context_node(fieldList)>
        del zserio_context_node

</#if>
    <#if fieldList?has_content>
        end_bitposition = bitposition

        <@choice_if "compound_initialize_offsets_field", true/>

        return end_bitposition
    <#else>
        return bitposition
    </#if>
</#if>

<#macro choice_read_field field indent packed index>
    <@compound_read_field field, name, indent, packed, index/>
</#macro>
    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
        <@choice_if "choice_read_field"/>
<#else>
        del zserio_reader
</#if>

    def read_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                    zserio_reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
    <#if !compound_needs_packing_context_node(fieldList)>
        del zserio_context_node

    </#if>
        <@choice_if "choice_read_field", true/>
<#else>
        del zserio_context_node
        del zserio_reader
</#if>
<#if withWriterCode>

<#macro choice_write_field field indent packed index>
    <@compound_write_field field, name, indent, packed, index/>
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

        <@choice_if "choice_write_field"/>
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
        <@choice_if "choice_write_field", true/>
    <#else>
        del zserio_context_node
        del zserio_writer
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field, name/>
</#list>
