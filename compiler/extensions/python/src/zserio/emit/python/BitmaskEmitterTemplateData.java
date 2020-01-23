package zserio.emit.python;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.FixedSizeType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType);

        importPackage("zserio"); // needed at least for hash code calculation

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        bitSize = createBitSize(bitmaskTypeInstantiation);

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(
                bitmaskTypeInstantiation, pythonExpressionFormatter);

        lowerBound = PythonLiteralFormatter.formatDecimalLiteral(getLowerBound(bitmaskTypeInstantiation));
        upperBound = PythonLiteralFormatter.formatDecimalLiteral(getUpperBound(bitmaskTypeInstantiation));

        final List<BitmaskValue> bitmaskValues = bitmaskType.getValues();
        values = new ArrayList<BitmaskValueData>(bitmaskValues.size());
        for (BitmaskValue bitmaskValue : bitmaskValues)
            values.add(new BitmaskValueData(bitmaskValue));
    }

    public String getBitSize()
    {
        return bitSize;
    }

    public RuntimeFunctionTemplateData getRuntimeFunction()
    {
        return runtimeFunction;
    }

    public String getLowerBound()
    {
        return lowerBound;
    }

    public String getUpperBound()
    {
        return upperBound;
    }

    public Iterable<BitmaskValueData> getValues()
    {
        return values;
    }

    private static BigInteger getUpperBound(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getUpperBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getUpperBound();
        throw new ZserioEmitException("Unexpected bitmask type instantiation!");
    }

    private static BigInteger getLowerBound(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getLowerBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getLowerBound();
        throw new ZserioEmitException("Unexpected bitmask type instantiation!");
    }

    public static class BitmaskValueData
    {
        public BitmaskValueData(BitmaskValue bitmaskValue) throws ZserioEmitException
        {
            name = bitmaskValue.getName();
            value = PythonLiteralFormatter.formatDecimalLiteral(bitmaskValue.getValue());
            isZero = bitmaskValue.getValue().equals(BigInteger.ZERO);
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public boolean getIsZero()
        {
            return isZero;
        }

        private final String name;
        private final String value;
        private final boolean isZero;
    }

    private static String createBitSize(TypeInstantiation typeInstantiation)
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            return PythonLiteralFormatter.formatDecimalLiteral(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return PythonLiteralFormatter.formatDecimalLiteral(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getMaxBitSize());
        }
        else
        {
            return null;
        }
    }

    private final String bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String lowerBound;
    private final String upperBound;
    private final List<BitmaskValueData> values;
}
