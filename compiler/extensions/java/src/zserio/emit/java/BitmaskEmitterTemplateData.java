package zserio.emit.java;

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
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.NativeIntegralType;
import zserio.emit.java.types.NativeLongType;

/**
 * The bitmask data used for FreeMarker template during bitmask file generation.
 */
public final class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType);

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final NativeIntegralType nativeBaseType =
                javaNativeMapper.getJavaIntegralType(bitmaskTypeInstantiation);
        baseJavaTypeName = nativeBaseType.getFullName();

        isLong = nativeBaseType instanceof NativeLongType;
        isSimpleType = nativeBaseType.isSimple();

        bitSize = createBitSize(bitmaskType);

        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(bitmaskTypeInstantiation,
                context.getJavaExpressionFormatter(), javaNativeMapper);

        final BigInteger lowerBound = getLowerBound(bitmaskTypeInstantiation);
        this.lowerBound = lowerBound.equals(nativeBaseType.getLowerBound()) ? null :
                nativeBaseType.formatLiteral(lowerBound);
        // upper bound is needed for negation since java uses signed types!
        final BigInteger upperBound = getUpperBound(bitmaskTypeInstantiation);
        this.upperBound = nativeBaseType.formatLiteral(upperBound);
        this.checkUpperBound = !upperBound.equals(nativeBaseType.getUpperBound());

        values = new ArrayList<BitmaskValueData>();
        for (BitmaskValue bitmaskValue: bitmaskType.getValues())
            values.add(new BitmaskValueData(nativeBaseType, bitmaskValue));
    }

    public String getBaseJavaTypeName()
    {
        return baseJavaTypeName;
    }

    public boolean getIsSimpleType()
    {
        return isSimpleType;
    }

    public boolean getIsLong()
    {
        return isLong;
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

    public boolean getCheckUpperBound()
    {
        return checkUpperBound;
    }

    public Iterable<BitmaskValueData> getValues()
    {
        return values;
    }

    private static String createBitSize(BitmaskType enumType) throws ZserioEmitException
    {
        final TypeInstantiation typeInstantiation = enumType.getTypeInstantiation();
        final ZserioType baseType = typeInstantiation.getBaseType();
        Integer bitSize = null;
        if (baseType instanceof FixedSizeType)
        {
            bitSize = ((FixedSizeType)baseType).getBitSize();
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            bitSize = ((DynamicBitFieldInstantiation)typeInstantiation).getMaxBitSize();
        }

        return (bitSize != null) ? JavaLiteralFormatter.formatDecimalLiteral(bitSize) : null;
    }

    private BigInteger getUpperBound(TypeInstantiation typeInstantiation) throws ZserioEmitException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getUpperBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getUpperBound();
        throw new ZserioEmitException("Unexpected bitmask type instantiation!");
    }

    private BigInteger getLowerBound(TypeInstantiation typeInstantiation) throws ZserioEmitException
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
        public BitmaskValueData(NativeIntegralType nativeBaseType, BitmaskValue bitmaskValue)
                throws ZserioEmitException
        {
            name = bitmaskValue.getName();
            value = nativeBaseType.formatLiteral(bitmaskValue.getValue());
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

    private final String baseJavaTypeName;
    private final boolean isSimpleType;
    private final boolean isLong;
    private final String bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String lowerBound;
    private final String upperBound;
    private final boolean checkUpperBound;
    private final List<BitmaskValueData> values;
}
