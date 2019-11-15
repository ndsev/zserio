package zserio.emit.java;

import java.math.BigInteger;

import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.ast.IntegerType;
import zserio.ast.StdIntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.NativeIntegralType;

public final class RangeCheckTemplateData
{
    public RangeCheckTemplateData(JavaNativeMapper javaNativeMapper, boolean withRangeCheckCode,
            String valueNameToCheck, ZserioType typeToCheck, boolean isTypeNullable,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        if (withRangeCheckCode)
        {
            final CommonRangeData commonRangeData = createCommonRangeData(javaNativeMapper, typeToCheck,
                    javaExpressionFormatter);
            // in setters, don't do range check if Zserio type has the same bounds as their native type
            if (commonRangeData != null && (commonRangeData.checkLowerBound || commonRangeData.checkUpperBound))
            {
                setterRangeData = new SetterRangeData(valueNameToCheck, commonRangeData.javaTypeName,
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

    public RangeCheckTemplateData(JavaNativeMapper javaNativeMapper, ZserioType typeToCheck,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        setterRangeData = null;
        final CommonRangeData commonRangeData = createCommonRangeData(javaNativeMapper, typeToCheck,
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
        public SetterRangeData(String name, String javaTypeName, boolean isTypeNullable,
                BitFieldWithExpression bitFieldWithExpression, String lowerBound, boolean checkUpperBound,
                String upperBound)
        {
            this.name = name;
            this.javaTypeName = javaTypeName;
            this.isTypeNullable = isTypeNullable;
            this.bitFieldWithExpression = bitFieldWithExpression;
            this.lowerBound = lowerBound;
            this.checkUpperBound = checkUpperBound;
            this.upperBound = upperBound;
        }

        public String getName()
        {
            return name;
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

        private final String                    name;
        private final String                    javaTypeName;
        private final boolean                   isTypeNullable;
        private final BitFieldWithExpression    bitFieldWithExpression;
        private final String                    lowerBound;
        private final boolean                   checkUpperBound;
        private final String                    upperBound;
    }

    public static class BitFieldWithExpression
    {
        public BitFieldWithExpression(BitFieldType bitFieldType,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            isSignedBitFieldStr = JavaLiteralFormatter.formatBooleanLiteral(bitFieldType.isSigned());
            lengthExpression = createBitFieldLengthExpression(bitFieldType, javaExpressionFormatter);
        }

        public String getIsSignedBitFieldStr()
        {
            return isSignedBitFieldStr;
        }

        public String getLengthExpression()
        {
            return lengthExpression;
        }

        private static String createBitFieldLengthExpression(BitFieldType bitFieldType,
                ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
        {
            final Expression lengthExpression = bitFieldType.getLengthExpression();
            return javaExpressionFormatter.formatGetter(lengthExpression);
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
            ZserioType typeToCheck, ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        // don't do range check for non-integer type
        IntegerType integerType = null;
        NativeIntegralType nativeType = null;
        if (typeToCheck instanceof IntegerType)
        {
            integerType = (IntegerType)typeToCheck;
            nativeType = javaNativeMapper.getJavaIntegralType(integerType);

            // don't do range check for BigInt
            if (nativeType.requiresBigInt())
                nativeType = null;
        }

        if (nativeType == null)
            return null;

        final String javaTypeName = nativeType.getFullName();
        final boolean isBoolType = (typeToCheck instanceof BooleanType);
        final boolean is64bitType = (integerType instanceof StdIntegerType) &&
                ((StdIntegerType)integerType).getBitSize() == 64;
        final BitFieldWithExpression bitFieldWithExpression = createBitFieldWithExpression(typeToCheck,
                javaExpressionFormatter);

        // Zserio types that have the same bounds as their native type are not checked
        final BigInteger zserioLowerBound = integerType.getLowerBound();
        final BigInteger nativeLowerBound = nativeType.getLowerBound();
        final boolean checkLowerBound = zserioLowerBound == null || nativeLowerBound.compareTo(zserioLowerBound) < 0;

        final BigInteger zserioUpperBound = integerType.getUpperBound();
        final BigInteger nativeUpperBound = nativeType.getUpperBound();
        final boolean checkUpperBound = zserioUpperBound == null || nativeUpperBound.compareTo(zserioUpperBound) > 0;

        final String lowerBound = zserioLowerBound != null ? nativeType.formatLiteral(zserioLowerBound) : null;
        final String upperBound = zserioUpperBound != null ? nativeType.formatLiteral(zserioUpperBound) : null;

        return new CommonRangeData(javaTypeName, isBoolType, is64bitType, bitFieldWithExpression,
                checkLowerBound, lowerBound, checkUpperBound, upperBound);
    }

    private static BitFieldWithExpression createBitFieldWithExpression(ZserioType typeToCheck,
            ExpressionFormatter javaExpressionFormatter) throws ZserioEmitException
    {
        if (typeToCheck instanceof BitFieldType)
        {
            final BitFieldType bitFieldType = (BitFieldType)typeToCheck;
            if (bitFieldType.getBitSize() == null)
                return new BitFieldWithExpression(bitFieldType, javaExpressionFormatter);
        }

        return null;
    }

    private final SetterRangeData   setterRangeData;
    private final SqlRangeData      sqlRangeData;
}
