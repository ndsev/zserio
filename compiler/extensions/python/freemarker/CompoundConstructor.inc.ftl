<#include "CompoundParameter.inc.ftl">
<#macro compound_constructors compoundConstructorsData>
    <#local constructorParamList><@compound_constructor_parameters compoundConstructorsData.compoundParametersData/></#local>
    <#if compoundConstructorsData.withWriterCode>
    def __init__(self, ${constructorParamList}):
        <@compound_constructor_parameter_assignments compoundConstructorsData.compoundParametersData/>

    </#if>
    @classmethod
    def fromReader(cls, reader, ${constructorParamList}):
    <#if compoundConstructorsData.withWriterCode>
        instance = cls(${constructorParamList})
    <#else>
        instance = cls()
        <@compound_instance_parameter_assignments compoundConstructorsData.compoundParametersData/>
    </#if>
        instance.read(reader)

        return instance

    @classmethod
    def fromFile(cls, filename, ${constructorParamList}):
        reader = zserio.BitStreamReader.fromFile(filename)
        return cls.fromReader(cls, ${constructorParamList}, reader)
</#macro>
