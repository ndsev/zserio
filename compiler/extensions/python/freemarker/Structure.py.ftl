<#include "FileHeader.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
<#if constructorAnnotatedParamList?has_content || fieldList?has_content>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> None:
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#list fieldList as field>
        self.<@field_member_name field/> = <#rt>
        <#if field.initializer??>
            ${field.initializer}<#t>
        <#elseif field.optional??>
            None<#t>
        <#else>
            <#if field.isBuiltinType>
            ${field.pythonTypeName}()<#t>
            <#elseif field.array??>
            zserio.array.Array(<@array_field_default_constructor_parameters field, withWriterCode/>)<#t>
            <#else>
            None<#t>
            </#if>
        </#if>
            <#lt> # type: <@field_annotation_type_name field, name/>
    </#list>
</#if>

<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    @classmethod
    def fromReader(
            cls: typing.Type['${name}'],
            reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance
<#if withWriterCode && fieldList?has_content>

    @classmethod
    def fromFields(
            cls: typing.Type['${name}']<#if constructorParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if><#list fieldList as field>,
            <@field_argument_name field/>: <@field_annotation_argument_type_name field, name/></#list>) -> '${name}':
        instance = cls(${constructorParamList})
    <#list fieldList as field>
        <#if withPythonProperties>
        instance.${field.propertyName} = <@field_argument_name field/>
        <#else>
        instance.${field.setterName}(<@field_argument_name field/>)
        </#if>
    </#list>

        return instance
</#if>

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
        <@compound_setter_field field/>
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

    def bitSizeOf(self, bitPosition: int = 0) -> int:
<#if fieldList?has_content>
        endBitPosition = bitPosition
    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return endBitPosition - bitPosition
<#else>
        del bitPosition

        return 0
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition: int) -> int:
    <#if fieldList?has_content>
        endBitPosition = bitPosition
        <#list fieldList as field>
            <@compound_initialize_offsets_field field, 2/>
        </#list>

        return endBitPosition
    <#else>
        return bitPosition
    </#if>
</#if>

<#assign needsReadNewLines=false/>
<#list fieldList as field>
    <#if has_field_any_read_check_code(field, name, 2)>
        <#assign needsReadNewLines=true/>
        <#break>
    </#if>
</#list>
    def read(self, reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
    <#list fieldList as field>
        <@compound_read_field field, name, withWriterCode, 2/>
        <#if field?has_next && needsReadNewLines>

        </#if>
    </#list>
<#else>
        del reader
</#if>
<#if withWriterCode>

    <#assign needsWriteNewLines=false/>
    <#list fieldList as field>
        <#if has_field_any_write_check_code(field, name, 2)>
            <#assign needsWriteNewLines=true/>
            <#break>
        </#if>
    </#list>
    def write(self, writer: zserio.BitStreamWriter, *, callInitializeOffsets: bool = True) -> None:
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            self.initializeOffsets(writer.getBitPosition())
        <#else>
        del callInitializeOffsets
        </#if>

        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    <#else>
        del writer
        del callInitializeOffsets
    </#if>
</#if>
<#list fieldList as field>
    <@define_offset_checker field, name/>
    <#if withWriterCode>
        <@define_offset_setter field/>
    </#if>
    <@define_element_creator field, name/>
</#list>
