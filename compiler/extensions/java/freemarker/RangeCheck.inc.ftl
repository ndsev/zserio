<#include "CompoundField.inc.ftl">
<#macro range_check rangeCheckData compoundName>
    <#if rangeCheckData.setterRangeData??>
        <#local rangeData=rangeCheckData.setterRangeData>
        // check range
        <#if rangeData.bitFieldWithExpression??>
        final int length = (int)(${rangeData.bitFieldWithExpression.lengthExpression});
        final long lowerBound = zserio.runtime.Util.getBitFieldLowerBound(
                length, ${rangeData.bitFieldWithExpression.isSignedBitFieldStr});
        final long upperBound = zserio.runtime.Util.getBitFieldUpperBound(
                length, ${rangeData.bitFieldWithExpression.isSignedBitFieldStr});
        <#else>
        final ${rangeData.javaTypeName} lowerBound = ${rangeData.lowerBound};
        final ${rangeData.javaTypeName} upperBound = ${rangeData.upperBound};
        </#if>
        if <#if rangeData.isTypeNullable>(<@field_argument_name rangeData.field/> != null && </#if><#rt>
            (<@field_argument_name rangeData.field/> < lowerBound<#if rangeData.checkUpperBound> || <@field_argument_name rangeData.field/> > upperBound</#if>)<#t>
            <#if rangeData.isTypeNullable>)</#if><#lt>
        {
            throw new zserio.runtime.ZserioError("Value " + <@field_argument_name rangeData.field/> +
                    " of ${compoundName}.${rangeData.field.name} exceeds the range of <" +
                    lowerBound + ".." + upperBound + ">!");
        }

    </#if>
</#macro>

<#macro sql_field_range_check rangeCheckData tableName fieldName>
    <#if rangeCheckData.sqlRangeData??>
        <#local rangeData=rangeCheckData.sqlRangeData>
        <#-- bitfields with expression are not supported in a sql_table -->
        <#if rangeData.isBoolType>
            final long lowerBound = 0;
            final long upperBound = 1;
        <#else>
            final long lowerBound = ${rangeData.lowerBound};
            final long upperBound = ${rangeData.upperBound};
        </#if>
            if (value < lowerBound || value > upperBound)
            {
                errors.add(new zserio.runtime.validation.ValidationError(tableName, "${fieldName}",
                        getRowKeyValues(resultSet),
                        zserio.runtime.validation.ValidationError.Type.VALUE_OUT_OF_RANGE,
                        "Value " + value + " of ${tableName}.${fieldName} exceeds the range of " +
                        lowerBound + ".." + upperBound));
                return false;
            }
    </#if>
</#macro>
