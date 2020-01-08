package zserio.emit.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.NativeIntegralType;

public class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioEmitException
    {
        super(context, bitmaskType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(bitmaskTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        baseCppTypeName = nativeBaseType.getFullName();

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(
                bitmaskTypeInstantiation, cppExpressionFormatter);

        final BigInteger upperBound = getUpperBound(bitmaskTypeInstantiation);
        mask = upperBound.equals(nativeBaseType.getUpperBound()) ? null :
                nativeBaseType.formatLiteral(upperBound);

        final List<BitmaskValue> bitmaskValues = bitmaskType.getValues();
        values = new ArrayList<BitmaskValueData>(bitmaskValues.size());
        for (BitmaskValue bitmaskValue : bitmaskValues)
            values.add(new BitmaskValueData(nativeBaseType, bitmaskValue));
    }

    public String getBaseCppTypeName()
    {
        return baseCppTypeName;
    }

    public RuntimeFunctionTemplateData getRuntimeFunction()
    {
        return runtimeFunction;
    }

    public String getMask()
    {
        return mask;
    }

    public Iterable<BitmaskValueData> getValues()
    {
        return values;
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
    };

    private final String baseCppTypeName;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String mask;
    private final List<BitmaskValueData> values;
}
