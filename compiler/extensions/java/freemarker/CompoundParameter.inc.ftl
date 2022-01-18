<#macro parameter_member_name param>
    ${param.name}_<#t>
</#macro>

<#macro parameter_argument_name param>
    ${param.name}_<#t>
</#macro>

<#macro compound_parameter_accessors compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    public ${compoundParameter.typeInfo.typeName} ${compoundParameter.getterName}()
    {
        return this.<@parameter_member_name compoundParameter/>;
    }

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    private final ${compoundParameter.typeInfo.typeName} <@parameter_member_name compoundParameter/>;
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

<#macro compound_hashcode_parameter parameter>
    <#if parameter.typeInfo.isSimple>
        <#if parameter.typeInfo.isLong>
            <#-- long type: use shifting -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                (int)(this.<@parameter_member_name parameter/> ^ (this.<@parameter_member_name parameter/> >>> 32));
        <#elseif parameter.typeInfo.isFloat>
            <#-- float type: use floatToIntBits() -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                java.lang.Float.floatToIntBits(this.<@parameter_member_name parameter/>);
        <#elseif parameter.typeInfo.isDouble>
            <#-- double type: use doubleToLongBits() -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                (int)(java.lang.Double.doubleToLongBits(this.<@parameter_member_name parameter/>) ^
                        (java.lang.Double.doubleToLongBits(this.<@parameter_member_name parameter/>) >>> 32));
        <#elseif parameter.typeInfo.isBoolean>
            <#-- bool type: convert it to int -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (this.<@parameter_member_name parameter/> ? 1 : 0);
        <#else>
            <#-- others: use implicit casting to int -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + this.<@parameter_member_name parameter/>;
        </#if>
    <#else>
        <#-- complex type: use hashCode() but account for possible null -->
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                ((this.<@parameter_member_name parameter/> == null) ? 0 : this.<@parameter_member_name parameter/>.hashCode());
    </#if>
</#macro>
