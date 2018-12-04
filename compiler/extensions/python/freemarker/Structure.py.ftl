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
        <#assign eqHasParenthesis = (compoundParametersData.list?size  + fieldList?size) gt 1/>
        if isinstance(other, ${name}):
            return <#if eqHasParenthesis>(</#if><#rt>
    <#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/><#if fieldList?has_content> and<#else><#if eqHasParenthesis>)</#if></#if>
    </#if>
    <#if fieldList?has_content>
                    <#if !compoundParametersData.list?has_content><#lt></#if><@structure_compare_fields fieldList, 5/><#if eqHasParenthesis>)</#if>
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
        self.<@field_member_name field/> = zserio.Array(<@array_field_constructor_parameters field/>)
        <#else>
        self.<@field_member_name field/> = <@field_argument_name field/>
        </#if>
    </#if>
    <#if field.optional??>

    def ${field.optional.indicatorName}(self):
        return (<#if field.optional.clause??>${field.optional.clause}<#else>${field.getterName}() != None</#if>)
    </#if>
</#list>

    def bitSizeOf(self, <#if !fieldList?has_content>_</#if>bitPosition=0):
<#if fieldList?has_content>
        endBitPosition = bitPosition
    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return endBitPosition - bitPosition
<#else>
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

    def read(self, <#if !fieldList?has_content>_</#if>reader):
<#if fieldList?has_content>
    <#list fieldList as field>
        <@compound_read_field field, name, 2/>
    </#list>
    <#if hasFieldWithConstraint>

        _checkConstraints() <#-- this could be done immediatelly after field reading -->
    </#if>
<#else>
        pass
</#if>
<#if withWriterCode>

    def write(self, <#if !fieldList?has_content>_</#if>writer, *, <#if !hasFieldWithOffset>_</#if>callInitializeOffsets=True):
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if callInitializeOffsets:
            initializeOffsets(writer.getBitPosition())

        </#if>
        <#if hasFieldWithConstraint>
        _checkConstraints()

        </#if>
        <#-- TODO range check is missing      <@range_check field.rangeCheckData, name/> -->
        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
        </#list>
    <#else>
        pass
    </#if>
</#if>
<#if hasFieldWithConstraint>

    def _checkConstraints(self):
    <#list fieldList as field>
        <@compound_check_constraint_field field, name, 2/>
    </#list>
</#if>
<#list fieldList as field>
    <@define_offset_checker name, field/>
    <#if withWriterCode>
        <@define_offset_setter field/>
    </#if>
    <@define_element_creator field/>
</#list>
