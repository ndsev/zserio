<#include "DocComment.inc.ftl">
<#macro parameter_member_name param>
    ${param.name}_<#t>
</#macro>

<#macro parameter_argument_name param>
    ${param.name}_<#t>
</#macro>

<#macro compound_set_parameters compoundParametersData>
    <#if compoundParametersData.list?has_content>
        <#list compoundParametersData.list as parameter>
        this.<@parameter_member_name parameter/> = <@parameter_argument_name parameter/>;
        </#list>
    </#if>
</#macro>

<#macro compound_parameter_comments compoundParametersData>
    <#list compoundParametersData.list as parameter>
     * @param <@parameter_argument_name parameter/> Value of the parameter {@link #${parameter.getterName}() ${parameter.name}}.
    </#list>
</#macro>

<#macro compound_parameter_accessors compoundParametersData>
    <#list compoundParametersData.list as parameter>
        <#if withCodeComments>
    /**
     * Gets the value of the parameter ${parameter.name}.
            <#if parameter.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner parameter.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @return The value of the parameter ${parameter.name}.
     */
        </#if>
    public ${parameter.typeInfo.typeFullName} ${parameter.getterName}()
    {
        return this.<@parameter_member_name parameter/>;
    }

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#list compoundParametersData.list as parameter>
    private final ${parameter.typeInfo.typeFullName} <@parameter_member_name parameter/>;
    </#list>
</#macro>

<#macro compound_compare_parameter parameter>
    <#if parameter.typeInfo.isSimple>
        <#if parameter.typeInfo.isFloat>
            <#-- float type: compare by floatToIntBits() to get rid of SpotBugs -->
java.lang.Float.floatToIntBits(this.<@parameter_member_name parameter/>) == java.lang.Float.floatToIntBits(that.<@parameter_member_name parameter/>)<#rt>
        <#elseif parameter.typeInfo.isDouble>
            <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
java.lang.Double.doubleToLongBits(this.<@parameter_member_name parameter/>) == java.lang.Double.doubleToLongBits(that.<@parameter_member_name parameter/>)<#rt>
        <#else>
            <#-- simple type: compare by == -->
this.<@parameter_member_name parameter/> == that.<@parameter_member_name parameter/><#rt>
        </#if>
    <#elseif parameter.typeInfo.isBytes>
((this.<@parameter_member_name parameter/> == null) ? that.<@parameter_member_name parameter/> == null : java.util.Arrays.equals(<@parameter_member_name parameter/>, that.<@parameter_member_name parameter/>))<#rt>
    <#elseif parameter.typeInfo.isEnum>
        <#-- enum type: compare by getValue() and == -->
((this.<@parameter_member_name parameter/> == null) ? that.<@parameter_member_name parameter/> == null : this.<@parameter_member_name parameter/>.getValue() == that.<@parameter_member_name parameter/>.getValue())<#rt>
    <#else>
        <#-- compare with equals() but account for possible null -->
((this.<@parameter_member_name parameter/> == null) ? that.<@parameter_member_name parameter/> == null : this.<@parameter_member_name parameter/>.equals(that.<@parameter_member_name parameter/>))<#rt>
    </#if>
</#macro>

<#macro compound_parameter_hash_code compoundParameterList>
    <#list compoundParametersData.list as parameter>
        result = zserio.runtime.HashCodeUtil.calcHashCode(result, ${parameter.getterName}());
    </#list>
</#macro>
