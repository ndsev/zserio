package zserio.extension.java;

import java.math.BigInteger;

import zserio.ast.BooleanType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.IntegerType;
import zserio.ast.StdIntegerType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.NativeIntegralType;

/**
 * FreeMarker template data for range checking.
 */
public final class RangeCheckTemplateData
{
    public RangeCheckTemplateData(CompoundFieldTemplateData field,
            JavaNativeMapper javaNativeMapper, boolean withRangeCheckCode,
            TypeInstantiation typeInstantiation, boolean isTypeNullable,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        if (withRangeCheckCode)
        {
            final CommonRangeData commonRangeData = createCommonRangeData(javaNativeMapper, typeInstantiation,
                    javaExpressionFormatter);
            // in setters, don't do range check if Zserio type has the same bounds as their native type
            if (commonRangeData != null && (commonRangeData.checkLowerBound || commonRangeData.checkUpperBound))
            {
                setterRangeData = new SetterRangeData(field, commonRangeData.javaTypeName,
                        isTypeNullable, commonRangeData.bitFieldWithExpression, commonRangeData.lowerBound,
                        commonRangeData.checkUpperBound, commonRangeData.upperBound);
            }
            else
            {
                setterRangeData = null;
            }
        }
        else
        {
            setterRangeData = null;
        }
        sqlRangeData = null;
    }

    public RangeCheckTemplateData(JavaNativeMapper javaNativeMapper, TypeInstantiation typeInstantiation,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        setterRangeData = null;
        final CommonRangeData commonRangeData = createCommonRangeData(javaNativeMapper, typeInstantiation,
                javaExpressionFormatter);
        // in SQL, don't do range check (u)int64 and variable-length bit fields
        if (commonRangeData != null && !commonRangeData.is64BitType &&
                commonRangeData.bitFieldWithExpression == null)
        {
            sqlRangeData = new SqlRangeData(commonRangeData.isBoolType, commonRangeData.lowerBound,
                    commonRangeData.upperBound);
        }
        else
        {
            sqlRangeData = null;
        }
    }

    public SetterRangeData getSetterRangeData()
    {
        return setterRangeData;
    }

    public SqlRangeData getSqlRangeData()
    {
        return sqlRangeData;
    }

    public static class SqlRangeData
    {
        public SqlRangeData(boolean isBoolType, String lowerBound, String upperBound)
        {
            this.isBoolType = isBoolType;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public boolean getIsBoolType()
        {
            return isBoolType;
        }

        public String getLowerBound()
        {
            return lowerBound;
        }

        public String getUpperBound()
        {
            return upperBound;
        }

        private final boolean   isBoolType;
        private final String    lowerBound;
        private final String    upperBound;
    }

    public static class SetterRangeData
    {
        public SetterRangeData(CompoundFieldTemplateData field, String javaTypeName, boolean isTypeNullable,
                BitFieldWithExpression bitFieldWithExpression, String lowerBound, boolean checkUpperBound,
                String upperBound)
        {
            this.field = field;
            this.javaTypeName = javaTypeName;
            this.isTypeNullable = isTypeNullable;
            this.bitFieldWithExpression = bitFieldWithExpression;
            this.lowerBound = lowerBound;
            this.checkUpperBound = checkUpperBound;
            this.upperBound = upperBound;
        }

        public CompoundFieldTemplateData getField()
        {
            return field;
        }

        public String getJavaTypeName()
        {
            return javaTypeName;
        }

        public boolean getIsTypeNullable()
        {
            return isTypeNullable;
        }

        public BitFieldWithExpression getBitFieldWithExpression()
        {
            return bitFieldWithExpression;
        }

        public String getLowerBound()
        {
            return lowerBound;
        }

        public boolean getCheckUpperBound()
        {
            return checkUpperBound;
        }

        public String getUpperBound()
        {
            return upperBound;
        }

        private final CompoundFieldTemplateData field;
        private final String                    javaTypeName;
        private final boolean                   isTypeNullable;
        private final BitFieldWithExpression    bitFieldWithExpression;
        private final String                    lowerBound;
        private final boolean                   checkUpperBound;
        private final String                    upperBound;
    }

    public static class BitFieldWithExpression
    {
        public BitFieldWithExpression(DynamicBitFieldInstantiation dynamicBitFieldInstantiation,
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
        {
            isSignedBitFieldStr = JavaLiteralFormatter.formatBooleanLiteral(
                    dynamicBitFieldInstantiation.getBaseType().isSigned());
            lengthExpression = javaExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());
        }

        public String getIsSignedBitFieldStr()
        {
            return isSignedBitFieldStr;
        }

        public String getLengthExpression()
        {
            return lengthExpression;
        }

        private final String    isSignedBitFieldStr;
        private final String    lengthExpression;
    }

    private static class CommonRangeData
    {
        public CommonRangeData(String javaTypeName, boolean isBoolType, boolean is64BitType,
                BitFieldWithExpression bitFieldWithExpression, boolean checkLowerBound, String lowerBound,
                boolean checkUpperBound, String upperBound)
        {
            this.javaTypeName = javaTypeName;
            this.isBoolType = isBoolType;
            this.is64BitType = is64BitType;
            this.bitFieldWithExpression = bitFieldWithExpression;
            this.checkLowerBound = checkLowerBound;
            this.lowerBound = lowerBound;
            this.checkUpperBound = checkUpperBound;
            this.upperBound = upperBound;
        }

        private final String                    javaTypeName;
        private final boolean                   isBoolType;
        private final boolean                   is64BitType;
        private final BitFieldWithExpression    bitFieldWithExpression;
        private final boolean                   checkLowerBound;
        private final String                    lowerBound;
        private final boolean                   checkUpperBound;
        private final String                    upperBound;
    }

    private static CommonRangeData createCommonRangeData(JavaNativeMapper javaNativeMapper,
            TypeInstantiation typeInstantiation, ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        // don't do range check for non-integer type
        IntegerType integerType = null;
        NativeIntegralType nativeType = null;
        final ZserioType baseType = typeInstantiation.getBaseType();
        if (baseType instanceof IntegerType)
        {
            integerType = (IntegerType)baseType;
            nativeType = javaNativeMapper.getJavaIntegralType(typeInstantiation);

            // don't do range check for BigInt
            if (nativeType.requiresBigInt())
                nativeType = null;
        }

        if (nativeType == null)
            return null;

        final String javaTypeName = nativeType.getFullName();
        final boolean isBoolType = (baseType instanceof BooleanType);
        final boolean is64bitType = (integerType instanceof StdIntegerType) &&
                ((StdIntegerType)integerType).getBitSize() == 64;
        final BitFieldWithExpression bitFieldWithExpression = createBitFieldWithExpression(typeInstantiation,
                javaExpressionFormatter);

        // Zserio types that have the same bounds as their native type are not checked
        final BigInteger zserioLowerBound = integerType.getLowerBound(typeInstantiation);
        final BigInteger nativeLowerBound = nativeType.getLowerBound();
        final boolean checkLowerBound = bitFieldWithExpression != null ||
                nativeLowerBound.compareTo(zserioLowerBound) < 0;

        final BigInteger zserioUpperBound = integerType.getUpperBound(typeInstantiation);
        final BigInteger nativeUpperBound = nativeType.getUpperBound();
        final boolean checkUpperBound = bitFieldWithExpression != null ||
                nativeUpperBound.compareTo(zserioUpperBound) > 0;

        final String lowerBound = nativeType.formatLiteral(zserioLowerBound);
        final String upperBound = nativeType.formatLiteral(zserioUpperBound);

        return new CommonRangeData(javaTypeName, isBoolType, is64bitType, bitFieldWithExpression,
                checkLowerBound, lowerBound, checkUpperBound, upperBound);
    }

    private static BitFieldWithExpression createBitFieldWithExpression(TypeInstantiation typeInstantiation,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        if (!(typeInstantiation instanceof DynamicBitFieldInstantiation))
            return null;

        return new BitFieldWithExpression((DynamicBitFieldInstantiation)typeInstantiation,
                javaExpressionFormatter);
    }

    private final SetterRangeData   setterRangeData;
    private final SqlRangeData      sqlRangeData;
}
