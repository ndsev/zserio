<#macro compound_constructor_parameters compoundParametersData>
    <#list compoundParametersData.list as parameter>
<@parameter_argument_name parameter/><#if parameter_has_next>, </#if><#rt>
    </#list>
</#macro>

<#macro compound_constructor_parameter_assignments compoundParametersData>
    <#list compoundParametersData.list as parameter>
        self.<@parameter_member_name parameter/> = <@parameter_argument_name parameter/>
    </#list>
</#macro>

<#macro compound_compare_parameters compoundParametersData indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as parameter>
self.<@parameter_member_name parameter/> == other.<@parameter_member_name parameter/><#rt>
        <#if parameter_has_next>
 and
${I}<#rt>
        </#if>
    </#list>
</#macro>

<#macro compound_hashcode_parameters compoundParametersData>
    <#list compoundParametersData.list as parameter>
        result = zserio.hashcode.calcHashCode(result, hash(self.<@parameter_member_name parameter/>))
    </#list>
</#macro>

<#macro compound_parameter_accessor parameter>
        return self.<@parameter_member_name parameter/>
</#macro>

<#macro parameter_member_name parameter>
_${parameter.name}_<#rt>
</#macro>

<#macro parameter_argument_name parameter>
${parameter.name}_<#rt>
</#macro>
