<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>

<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
class ${name}:
    def __init__(self<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        <@compound_constructor_parameter_assignments compoundParametersData/>
        self._choiceTag = self.UNDEFINED_CHOICE
        self._choice = None

    @classmethod
    def fromReader(cls, reader<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance

    def __eq__(self, other):
        if isinstance(other, ${name}):
            return (<#rt>
<#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/> and
</#if>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt>self._choiceTag == other._choiceTag and
                    self._choice == other._choice)

        return False

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calcHashCode(result, hash(self._choiceTag))
        result = zserio.hashcode.calcHashCode(result, hash(self._choice))

        return result
<#list compoundParametersData.list as parameter>

    def ${parameter.getterName}(self):
        <@compound_parameter_accessor parameter/>
</#list>
<#list fieldList as field>

    def ${field.getterName}(self):
        <@compound_getter_field field/>
    <#if withWriterCode>

    def ${field.setterName}(self, <@field_argument_name field/>):
        self._choiceTag = self.<@choice_tag_name field/>
        <@compound_setter_field field/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self):
        return ${function.resultExpression}
</#list>

    def choiceTag(self):
        return self._choiceTag

<#macro union_if memberActionMacroName>
    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == self.<@choice_tag_name field/>:
            <@.vars[memberActionMacroName] field, 3/>
    </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")
</#macro>
    def bitSizeOf(self, bitPosition=0):
<#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition += zserio.bitsizeof.getBitSizeOfVarUInt64(self._choiceTag)

        <@union_if "compound_bitsizeof_field"/>

        return endBitPosition - bitPosition
<#else>
        del bitPosition

        return 0
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition):
    <#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition += zserio.bitsizeof.getBitSizeOfVarUInt64(self._choiceTag)

        <@union_if "compound_initialize_offsets_field"/>

        return endBitPosition
    <#else>
        return bitPosition
    </#if>
</#if>

<#macro union_read_field field indent>
    <@compound_read_field field, name, withWriterCode, indent/>
</#macro>
    def read(self, reader):
<#if fieldList?has_content>
        self._choiceTag = reader.readVarUInt64()

        <@union_if "union_read_field"/>
<#else>
        del reader
</#if>
<#if withWriterCode>

    <#macro union_write_field field indent>
        <@compound_write_field field, name, indent/>
    </#macro>
    def write(self, writer, *, callInitializeOffsets=True):
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            self.initializeOffsets(writer.getBitPosition())
        <#else>
        del callInitializeOffsets
        </#if>

        writer.writeVarUInt64(self._choiceTag)

        <@union_if "union_write_field"/>
    <#else>
        del writer
        del callInitializeOffsets
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field/>
</#list>

<#list fieldList as field>
    <@choice_tag_name field/> = ${field?index}
</#list>
    <#-- Don't use CHOICE_UNDEFINED name because of clashing with generated tags from fields. -->
    UNDEFINED_CHOICE = -1
