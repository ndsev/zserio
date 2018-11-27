<#include "FileHeader.inc.ftl"/>
<#include "CompoundConstructor.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundFunction.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<@file_header generatorDescription/>

<@all_imports packageImports typeImports/>

class ${name}():
    <@compound_constructors compoundConstructorsData/>

    def __eq__(self, other):
        return (<@compound_compare_parameters compoundParametersData, 4/><#if fieldList?has_content> and</#if>
<#list fieldList as field>
                <@compound_compare_field field, 4/><#if field_has_next> and<#else>)</#if>
</#list>

    def __hash__(self):
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
<#list fieldList as field>
        <@compound_hashcode_field field/>
</#list>

        return result

    def bitSizeOf(self, bitPosition=0):
        endBitPosition = bitPosition
<#list fieldList as field>
        <@compound_bitsizeof_field field/>
</#list>

        return endBitPosition - bitPosition

    def initializeOffsets(self, bitPosition):
        endBitPosition = bitPosition
<#list fieldList as field>
        <@compound_initialize_offsets_field field/>
</#list>

        # alternatively if there are no offsets (Java and C++ are different)
        # return bitPosition + self.bitSizeOf(bitPosition)

        return endBitPosition

    <@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    def ${field.getterName}(self):
        return self.<@field_member_name field/>

</#list>
