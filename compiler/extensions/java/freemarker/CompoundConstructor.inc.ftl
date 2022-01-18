<#include "CompoundParameter.inc.ftl">
<#macro compound_constructors compoundConstructorsData>
    <#local name=compoundConstructorsData.compoundName>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData/></#local>
    <#if withWriterCode>
    public ${name}(${constructorArgumentTypeList})
    {
        <@compound_constructors_set_parameters compoundConstructorsData/>
    }

    </#if>
    public ${name}(java.io.File file<#if constructorArgumentTypeList?has_content>,${constructorArgumentTypeList}</#if>)
            throws java.io.IOException
    {
        <@compound_constructors_set_parameters compoundConstructorsData/>
        <#if constructorArgumentTypeList?has_content>

        </#if>
        final zserio.runtime.io.FileBitStreamReader in = new zserio.runtime.io.FileBitStreamReader(file);
        read(in);
        in.close();
    }

    public ${name}(zserio.runtime.io.BitStreamReader in<#if constructorArgumentTypeList?has_content>,${constructorArgumentTypeList}</#if>)
            throws java.io.IOException
    {
        <@compound_constructors_set_parameters compoundConstructorsData/>
        <#if constructorArgumentTypeList?has_content>

        </#if>
        read(in);
    }

    public ${name}(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in<#rt>
            <#lt><#if constructorArgumentTypeList?has_content>,${constructorArgumentTypeList}</#if>)
            throws java.io.IOException
    {
        <@compound_constructors_set_parameters compoundConstructorsData/>
        <#if constructorArgumentTypeList?has_content>

        </#if>
        read(contextNode, in);
    }

</#macro>

<#macro compound_constructors_set_parameters compoundConstructorsData>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData/></#local>
    <#if constructorArgumentTypeList?has_content>
        <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
        this.<@parameter_member_name compoundParameter/> = <@parameter_argument_name compoundParameter/>;
        </#list>
    </#if>
</#macro>

<#macro compound_constructor_argument_type_list compoundConstructorsData>
    <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>

            ${compoundParameter.typeInfo.typeFullName} <@parameter_argument_name compoundParameter/><#if compoundParameter_has_next>,</#if><#rt>
    </#list>
</#macro>

<#macro compound_constructor_argument_list compoundConstructorsData>
    <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
<@parameter_argument_name compoundParameter/><#if compoundParameter_has_next>, </#if><#rt>
    </#list>
</#macro>
