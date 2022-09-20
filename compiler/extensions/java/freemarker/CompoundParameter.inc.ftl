<#macro parameter_member_name param>
    ${param.name}_<#t>
</#macro>

<#macro parameter_argument_name param>
    ${param.name}_<#t>
</#macro>

<#macro compound_parameter_accessors compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    public ${compoundParameter.typeInfo.typeFullName} ${compoundParameter.getterName}()
    {
        return this.<@parameter_member_name compoundParameter/>;
    }

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    private final ${compoundParameter.typeInfo.typeFullName} <@parameter_member_name compoundParameter/>;
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
    <#elseif parameter.typeInfo.isEnum>
        <#-- enum type: compare by getValue() and == -->
((this.<@parameter_member_name parameter/> == null) ? that.<@parameter_member_name parameter/> == null : this.<@parameter_member_name parameter/>.getValue() == that.<@parameter_member_name parameter/>.getValue())<#rt>
    <#else>
        <#-- compare with equals() but account for possible null -->
((this.<@parameter_member_name parameter/> == null) ? that.<@parameter_member_name parameter/> == null : this.<@parameter_member_name parameter/>.equals(that.<@parameter_member_name parameter/>))<#rt>
    </#if>
</#macro>

<#macro compound_parameter_hash_code compoundParameterList>
    <#list compoundParametersData.list as compoundParameter>
        result = zserio.runtime.HashCodeUtil.calcHashCode(result, ${compoundParameter.getterName}());
    </#list>
</#macro>
