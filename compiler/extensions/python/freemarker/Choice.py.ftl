<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
selector == (${expressionList?first})<#rt>
    <#else>
selector in (<#list expressionList as expression>${expression}<#if expression?has_next>, </#if></#list>)<#rt>
    </#if>
</#macro>
<#macro choice_if memberActionMacroName>
        selector = ${selector}
    <#list caseMemberList as caseMember>
        <#if caseMember?has_next || !isDefaultUnreachable>
        <#if caseMember?is_first>if <#else>elif </#if><@choice_selector_condition caseMember.expressionList/>:
        <#else>
        else:
        </#if>
            <@.vars[memberActionMacroName] caseMember, 3/>
    </#list>
    <#if !isDefaultUnreachable>
        else:
        <#if defaultMember??>
            <@.vars[memberActionMacroName] defaultMember, 3/>
        <#else>
            raise zserio.PythonRuntimeException("No match in choice ${name}!")
        </#if>
    </#if>
</#macro>

class ${name}:
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> None:
        <@compound_constructor_parameter_assignments compoundParametersData/>
        self._choice = None # type: typing.Any

<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    @classmethod
    def fromReader(
            cls: typing.Type['${name}'],
            reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return (<@compound_compare_parameters compoundParametersData, 5/> and
                    self._choice == other._choice)

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calcHashCode(result, hash(self._choice))

        return result
<#list compoundParametersData.list as parameter>

    <#if withPythonProperties>
    @property
    def ${parameter.propertyName}<#rt>
    <#else>
    def ${parameter.getterName}<#rt>
    </#if>
    <#lt>(self) -> ${parameter.pythonTypeName}:
        <@compound_parameter_accessor parameter/>
</#list>
<#list fieldList as field>

    <#if withPythonProperties>
    @property
    def ${field.propertyName}<#rt>
    <#else>
    def ${field.getterName}<#rt>
    </#if>
    <#lt>(self) -> <@field_annotation_argument_type_name field, name/>:
        <@compound_getter_field field/>
    <#if withWriterCode>

        <#if withPythonProperties>
    @${field.propertyName}.setter
    def ${field.propertyName}<#rt>
        <#else>
    def ${field.setterName}<#rt>
        </#if>
    <#lt>(self, <@field_argument_name field/>: <@field_annotation_argument_type_name field, name/>) -> None:
        <@compound_setter_field field, withWriterCode, 2/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self) -> ${function.returnPythonTypeName}:
        return ${function.resultExpression}
</#list>

<#macro choice_bitsizeof_member member indent>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}pass
    </#if>
</#macro>
    def bitSizeOf(self, bitPosition: int = 0) -> int:
<#if fieldList?has_content>
        endBitPosition = bitPosition

        <@choice_if "choice_bitsizeof_member"/>

        return endBitPosition - bitPosition
<#else>
        del bitPosition

        return 0
</#if>
<#if withWriterCode>

    <#macro choice_initialize_offsets_member member indent>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#if member.compoundField??>
            <@compound_initialize_offsets_field member.compoundField, indent/>
        <#else>
            <#lt>${I}pass
        </#if>
    </#macro>
    def initializeOffsets(self, bitPosition: int) -> int:
    <#if fieldList?has_content>
        endBitPosition = bitPosition

        <@choice_if "choice_initialize_offsets_member"/>

        return endBitPosition
    <#else>
        return bitPosition
    </#if>
</#if>

<#macro choice_read_member member indent>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, withWriterCode, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}pass
    </#if>
</#macro>
    def read(self, reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
        <@choice_if "choice_read_member"/>
<#else>
        del reader
</#if>
<#if withWriterCode>

    <#macro choice_write_member member indent>
        <#if member.compoundField??>
            <@compound_write_field member.compoundField, name, indent/>
        <#else>
            <#local I>${""?left_pad(indent * 4)}</#local>
            <#lt>${I}pass
        </#if>
    </#macro>
    def write(self, writer: zserio.BitStreamWriter, *, callInitializeOffsets: bool = True) -> None:
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            self.initializeOffsets(writer.getBitPosition())
        <#else>
        del callInitializeOffsets
        </#if>

        <@choice_if "choice_write_member"/>
    <#else>
        del writer
        del callInitializeOffsets
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field, name/>
</#list>
