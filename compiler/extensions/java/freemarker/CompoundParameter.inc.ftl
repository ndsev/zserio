<#macro compound_parameter_accessors compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    public ${compoundParameter.javaTypeName} ${compoundParameter.getterName}()
    {
        return this.${compoundParameter.name};
    }

    </#list>
</#macro>

<#macro compound_parameter_members compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
    private final ${compoundParameter.javaTypeName} ${compoundParameter.name};
    </#list>
</#macro>

<#macro compound_compare_parameter parameter>
    <#if parameter.isSimpleType>
        <#if parameter.isFloat>
            <#-- float type: compare by floatToIntBits() to get rid of SpotBugs -->
Float.floatToIntBits(this.${parameter.name}) == Float.floatToIntBits(__that.${parameter.name})<#rt>
        <#elseif parameter.isDouble>
            <#-- double type: compare by doubleToLongBits() to get rid of SpotBugs -->
Double.doubleToLongBits(this.${parameter.name}) == Double.doubleToLongBits(__that.${parameter.name})<#rt>
        <#else>
            <#-- simple type: compare by == -->
this.${parameter.name} == __that.${parameter.name}<#rt>
        </#if>
    <#elseif parameter.isEnum>
        <#-- enum type: compare by getValue() and == -->
((this.${parameter.name} == null) ? __that.${parameter.name} == null : this.${parameter.name}.getValue() == __that.${parameter.name}.getValue())<#rt>
    <#else>
        <#-- compare with equals() but account for possible null -->
((this.${parameter.name} == null) ? __that.${parameter.name} == null : this.${parameter.name}.equals(__that.${parameter.name}))<#rt>
    </#if>
</#macro>

<#macro compound_hashcode_parameter parameter>
    <#if parameter.isSimpleType>
        <#if parameter.isLong>
            <#-- long type: use shifting -->
        __result = Util.HASH_PRIME_NUMBER * __result + (int) (${parameter.name} ^ (${parameter.name} >>> 32));
        <#elseif parameter.isFloat>
            <#-- float type: use floatToIntBits() -->
        __result = Util.HASH_PRIME_NUMBER * __result + Float.floatToIntBits(${parameter.name});
        <#elseif parameter.isDouble>
            <#-- double type: use doubleToLongBits() -->
        __result = Util.HASH_PRIME_NUMBER * __result + (int) (Double.doubleToLongBits(${parameter.name}) ^
                (Double.doubleToLongBits(${parameter.name}) >>> 32));
        <#elseif parameter.isBool>
            <#-- bool type: convert it to int -->
        __result = Util.HASH_PRIME_NUMBER * __result + (${parameter.name} ? 1 : 0);
        <#else>
            <#-- others: use implicit casting to int -->
        __result = Util.HASH_PRIME_NUMBER * __result + ${parameter.name};
        </#if>
    <#else>
        <#-- complex type: use hashCode() but account for possible null -->
        __result = Util.HASH_PRIME_NUMBER * __result + ((${parameter.name} == null) ? 0 : ${parameter.name}.hashCode());
    </#if>
</#macro>
