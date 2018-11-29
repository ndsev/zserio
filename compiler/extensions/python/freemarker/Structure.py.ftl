<#include "FileHeader.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundFunction.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>

<#assign hasFieldWithConstraint=false/>
<#list fieldList as field>
    <#if field.constraint??>
        <#assign hasFieldWithConstraint=true/>
        <#break>
    </#if>
</#list>
class ${name}():
<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
<#if constructorParamList?has_content || fieldList?has_content>
    def __init__(self<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#list fieldList as field>
        <@field_member_name field/> = None <#-- TODO: initialize fields with default values or None always in ctor!!! -->
    </#list>
</#if>

    @classmethod
    def fromReader(cls, <#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        instance = cls(${constructorParamList})
        instance.read(reader)

        return instance
<#if withWriterCode && fieldList?has_content>

    @classmethod
    def fromFields(cls<#if constructorParamList?has_content>, ${constructorParamList}</#if><#rt>
                   <#lt><#list fieldList as field>, <@field_argument_name field/></#list>)
        instance = cls(${constructorParamList})
    <#list fieldList as field>
        instance.<@field_member_name field/> = <@field_argument_name field/>
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
        if isinstance(other, ${name}):
            return (<#rt>
    <#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/><#if fieldList?has_content> and</#if>
                    <@structure_compare_fields fieldList, 5/>)
    <#else>
                    <#lt><@structure_compare_fields fieldList, 5/>)
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
        return self.<@field_member_name field/><#if field.array??>.getRawArray()</#if>
    <#if withWriterCode>
    
    def ${field.setterName}(self, <@field_argument_name field/>):
        <#if field.array??>
        self.<@field_member_name field/> = zserio.Array(${field.array.traitsName}(<#-- TODO -->), <@field_argument_name field/><#rt>
            <#lt><#if field.array.isImplicit>, isImplicit=True<#rt>
            <#lt><#elseif !field.array.length??>, isAuto=True</#if><#rt>
            <#if field.offset?? && field.offset.containsIndex>
                <#lt>, setOffsetMethod=None, checkOffsetMethod=None<#rt>  <#-- TODO -->
            </#if>
            <#lt>)
        <#else>
        self.<@field_member_name field/> = <@field_argument_name field/>
        </#if>
    </#if>
    <#if field.optional??>
    
    def ${field.optional.indicatorName}(self):
        return (<#if field.optional.clause??>${field.optional.clause}<#else>${field.getterName}() != None</#if>)
    </#if>
</#list>

    def bitSizeOf(self, bitPosition=0):
        endBitPosition = bitPosition
<#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
</#list>

        return endBitPosition - bitPosition
<#if withWriterCode>

    def initializeOffsets(self, bitPosition):
        endBitPosition = bitPosition
        <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2/>
        </#list>

        return endBitPosition
</#if>
    
    def read(self, reader):
<#list fieldList as field>
        <@compound_read_field field, name, 2/>
</#list>
    <#if hasFieldWithConstraint>
        <#--  TODO check constraints is missing -->
    </#if>
<#if withWriterCode>

    def write(self, writer, *, callInitializeOffsets=True):
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            initializeOffsets(writer.getBitPosition())
            
        </#if>
        <#if hasFieldWithConstraint>
        <#-- TODO check constraints is missing -->
        <#-- TODO range check is missing      <@range_check field.rangeCheckData, name/> -->
        <#-- TODO check everthing all -->
        <#-- TODO check offsets -->
        <#-- TODO use private methods! -->
        <#--newline-->
        </#if>
        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
        </#list>
    </#if>
</#if>
