<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports symbolImports typeImports/>

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
        self._choiceTag: int = self.UNDEFINED_CHOICE
        self._choice: typing.Any = None
<#if fieldList?has_content>
    <#list fieldList as field>
        if <@field_argument_name field/> is not None:
        <#if !field?is_first>
            if self._choiceTag != self.UNDEFINED_CHOICE:
                raise zserio.PythonRuntimeException("Calling constructor of union ${name} is ambiguous!")
        </#if>
            self._choiceTag = self.${getChoiceTagName(field.name)}
            <@compound_setter_field field, withWriterCode, 3/>
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

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return (<#rt>
<#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/> and
</#if>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt>self._choiceTag == other._choiceTag and
                    self._choice == other._choice)

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calcHashCode(result, hash(self._choiceTag))
        result = zserio.hashcode.calcHashCode(result, hash(self._choice))

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
        self._choiceTag = self.${getChoiceTagName(field.name)}
        <@compound_setter_field field, withWriterCode, 2/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self) -> ${function.returnPythonTypeName}:
        return ${function.resultExpression}
</#list>

    @property
    def choiceTag(self) -> int:
        return self._choiceTag

<#macro union_if memberActionMacroName>
    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == self.${getChoiceTagName(field.name)}:
            <@.vars[memberActionMacroName] field, 3/>
    </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")
</#macro>
    def bitSizeOf(self, bitPosition: int = 0) -> int:
<#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition += zserio.bitsizeof.getBitSizeOfVarSize(self._choiceTag)

        <@union_if "compound_bitsizeof_field"/>

        return endBitPosition - bitPosition
<#else>
        del bitPosition

        return 0
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition: int) -> int:
    <#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition += zserio.bitsizeof.getBitSizeOfVarSize(self._choiceTag)

        <@union_if "compound_initialize_offsets_field"/>

        return endBitPosition
    <#else>
        return bitPosition
    </#if>
</#if>

<#macro union_read_field field indent>
    <@compound_read_field field, name, withWriterCode, indent/>
</#macro>
    def read(self, reader: zserio.BitStreamReader) -> None:
<#if fieldList?has_content>
        self._choiceTag = reader.readVarSize()

        <@union_if "union_read_field"/>
<#else>
        del reader
</#if>
<#if withWriterCode>

    <#macro union_write_field field indent>
        <@compound_write_field field, name, indent/>
    </#macro>
    def write(self, writer: zserio.BitStreamWriter, *, callInitializeOffsets: bool = True) -> None:
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            self.initializeOffsets(writer.getBitPosition())
        <#else>
        del callInitializeOffsets
        </#if>

        writer.writeVarSize(self._choiceTag)

        <@union_if "union_write_field"/>
    <#else>
        del writer
        del callInitializeOffsets
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field, name/>
</#list>

<#list fieldList as field>
    ${getChoiceTagName(field.name)} = ${field?index}
</#list>
    <#-- Don't use CHOICE_UNDEFINED name because of clashing with generated tags from fields. -->
    ${undefinedChoiceTagName} = -1
