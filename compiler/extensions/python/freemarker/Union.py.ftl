<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<@file_header generatorDescription/>
<@all_imports packageImports typeImports/>

class ${name}:
<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    def __init__(self<#if constructorParamList?has_content>, ${constructorParamList}</#if>):
        <@compound_constructor_parameter_assignments compoundParametersData/>
        self._choiceTag = CHOICE_TAG_UNDEFINED
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
                    self._choiceTag == other._choiceTag and self._choice == other._choice)
<#else>
                    <#lt>self._choiceTag == other._choiceTag and self._choice == other._choice)
</#if>

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
        if (self._choiceTag != <@choice_tag_name field/>:
            raise zserio.PythonRuntimeException("Cannot get field ${field.name} from union ${name}: "
                                                "choiceTag " + self._choiceTag + " != " +
                                                <@choice_tag_name field/> + "!")

        return self._choice
    <#if withWriterCode>
    
    def ${field.setterName}(self, <@field_argument_name field/>):
        self._choiceTag = <@choice_tag_name field/>
        <#if field.array??>
        self._choice = zserio.Array(${field.array.traitsName}(<#-- TODO -->), <@field_argument_name field/><#rt>
            <#lt><#if field.array.isImplicit>, isImplicit=True<#rt>
            <#lt><#elseif !field.array.length??>, isAuto=True</#if><#rt>
            <#if field.offset?? && field.offset.containsIndex>
                <#lt>, setOffsetMethod=None, checkOffsetMethod=None<#rt>  <#-- TODO -->
            </#if>
            <#lt>)
        <#else>
        self._choice = <@field_argument_name field/>)
        </#if>
    </#if>
</#list>

    def choiceTag(self):
        return self._choiceTag

    def bitSizeOf(self, <#if fieldList?has_content>_</#if>bitPosition=0):
<#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition = zserio.bitsizeof.getBitSizeOfVarUInt64(self._choiceTag)

    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == <@choice_tag_name field/>:
            <@compound_bitsizeof_field field, 3/>
    </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")

        return endBitPosition - bitPosition;
<#else>
        return 0
</#if>
<#if withWriterCode>

    def initializeOffsets(self, bitPosition):
    <#if fieldList?has_content>
        endBitPosition = bitPosition

        endBitPosition += zserio.bitsizeof.getBitSizeOfVarUInt64(self._choiceTag)

        <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == <@choice_tag_name field/>:
            <@compound_initialize_offsets_field field, 3/>
        </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")

        return endBitPosition 
    <#else>
        return bitPosition
    </#if>
</#if>

    def read(self, reader):
<#if fieldList?has_content>
        self._choiceTag = reader.readVarUInt64() <#-- TODO: convert to int??? -->

    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == <@choice_tag_name field/>:
            <@compound_read_field field, name, 3/>
            <#--<@compound_check_constraint_field field, name, 3/> TODO -->
    </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")
</#if>
<#if withWriterCode>

    def write(self, <#if fieldList?has_content>_</#if>writer, *, <#if fieldList?has_content>_</#if>callInitializeOffsets=True):
    <#if fieldList?has_content>
<#-- TODO range check is missing      <@range_check field.rangeCheckData, name/> -->
        <#if hasFieldWithOffset>
        if callInitializeOffests:
            initializeOffsets(writer.getBitPosition())

        </#if>
        writer.writeVarUInt64(self._choiceTag)
        
        <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choiceTag == <@choice_tag_name field/>:
            <#--<@compound_check_constraint_field field, name, 3/>-->
            <@compound_write_field field, name, 3/>
        </#list>
        else:
            raise zserio.PythonRuntimeException("No match in union ${name}!")
    <#else>
        pass
    </#if>
</#if>

<#list fieldList as field>
    <@choice_tag_name field/> = ${field?index}
</#list>
    CHOICE_TAG_UNDEFINED = -1
