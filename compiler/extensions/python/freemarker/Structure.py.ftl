<#include "FileHeader.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>

class ${name}():
<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
<#if constructorParamList?has_content || fieldList?has_content>
    def __init__(self<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#list fieldList as field>
        self.<@field_member_name field/> = <#if field.initializer??>${field.initializer}<#else>None</#if>
    </#list>
</#if>

    @classmethod
    def fromReader(cls, reader<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance
<#if withWriterCode && fieldList?has_content>

    @classmethod
    def fromFields(cls<#if constructorParamList?has_content>, ${constructorParamList}</#if><#rt>
                   <#lt><#list fieldList as field>, <@field_argument_name field/></#list>):
        instance = cls(${constructorParamList})
    <#list fieldList as field>
        instance.${field.setterName}(<@field_argument_name field/>)
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
    def __eq__(self, other):
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

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
<#list fieldList as field>
        <@compound_hashcode_field field/>
</#list>

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
        <@compound_setter_field field/>
    </#if>
    <#if field.optional??>

    def ${field.optional.indicatorName}(self):
        return <#if field.optional.clause??>${field.optional.clause}<#else>not self.<@field_member_name field/> is None</#if>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.name}(self):
        return ${function.resultExpression}
</#list>

    def bitSizeOf(self, bitPosition=0):
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

    def initializeOffsets(self, bitPosition):
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
    def read(self, reader):
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
    def write(self, writer, *, callInitializeOffsets=True):
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
    <@define_offset_checker name, field/>
    <#if withWriterCode>
        <@define_offset_setter field/>
    </#if>
    <@define_element_creator field/>
</#list>
