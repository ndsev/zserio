<#macro range_check rangeCheckData compoundName>
    <#if rangeCheckData.setterRangeData??>
        <#local rangeData=rangeCheckData.setterRangeData>
        // check range
        <#if rangeData.bitFieldWithExpression??>
        final int __length = ${rangeData.bitFieldWithExpression.lengthExpression};
        final long __lowerBound = zserio.runtime.Util.getBitFieldLowerBound(
                __length, ${rangeData.bitFieldWithExpression.isSignedBitFieldStr});
        final long __upperBound = zserio.runtime.Util.getBitFieldUpperBound(
                __length, ${rangeData.bitFieldWithExpression.isSignedBitFieldStr});
        <#else>
        final ${rangeData.javaTypeName} __lowerBound = ${rangeData.lowerBound};
        final ${rangeData.javaTypeName} __upperBound = ${rangeData.upperBound};
        </#if>
        if <#if rangeData.isTypeNullable>(${rangeData.name} != null && </#if><#rt>
            (${rangeData.name} < __lowerBound<#if rangeData.checkUpperBound> || ${rangeData.name} > __upperBound</#if>)<#t>
            <#if rangeData.isTypeNullable>)</#if><#lt>
        {
            throw new zserio.runtime.ZserioError("Value " + ${rangeData.name} +
                    " of ${compoundName}.${rangeData.name} exceeds the range of <" +
                    __lowerBound + ".." + __upperBound + ">!");
        }

    </#if>
</#macro>

<#macro sql_field_range_check rangeCheckData tableName fieldName>
    <#if rangeCheckData.sqlRangeData??>
        <#local rangeData=rangeCheckData.sqlRangeData>
    <#-- bitfields with expression are not supported in a sql_table -->
        <#if rangeData.isBoolType>
            final long __lowerBound = 0;
            final long __upperBound = 1;
        <#else>
            final long __lowerBound = ${rangeData.lowerBound};
            final long __upperBound = ${rangeData.upperBound};
        </#if>
            if (value < __lowerBound || value > __upperBound)
            {
                errors.add(new zserio.runtime.validation.ValidationError(__tableName, "${fieldName}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.VALUE_OUT_OF_RANGE,
                        "Value " + value + " of ${tableName}.${fieldName} exceeds the range of " +
                        __lowerBound + ".." + __upperBound));
                return false;
            }
    </#if>
</#macro>
