package zserio.extension.cpp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.DocComment;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * FreeMarker template data for BitmaskEmitter.
 */
public class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioExtensionException
    {
        super(context, bitmaskType, bitmaskType.getDocComments());

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(bitmaskTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        underlyingTypeInfo = new NativeIntegralTypeInfoTemplateData(nativeBaseType, bitmaskTypeInstantiation);

        bitSize = BitSizeTemplateData.create(context, bitmaskTypeInstantiation, this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(context, bitmaskTypeInstantiation, this);

        final BigInteger upperBound = getUpperBound(bitmaskTypeInstantiation);
        this.upperBound = upperBound.equals(nativeBaseType.getUpperBound()) ? null :
                nativeBaseType.formatLiteral(upperBound);

        final List<BitmaskValue> bitmaskValues = bitmaskType.getValues();
        values = new ArrayList<BitmaskValueData>(bitmaskValues.size());
        for (BitmaskValue bitmaskValue : bitmaskValues)
            values.add(new BitmaskValueData(nativeBaseType, bitmaskValue));
    }

    public NativeIntegralTypeInfoTemplateData getUnderlyingTypeInfo()
    {
        return underlyingTypeInfo;
    }

    public BitSizeTemplateData getBitSize()
    {
        return bitSize;
    }

    public RuntimeFunctionTemplateData getRuntimeFunction()
    {
        return runtimeFunction;
    }

    public String getUpperBound()
    {
        return upperBound;
    }

    public Iterable<BitmaskValueData> getValues()
    {
        return values;
    }

    private static BigInteger getUpperBound(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getUpperBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getUpperBound();
        throw new ZserioExtensionException("Unexpected bitmask type instantiation!");
    }

    public static class BitmaskValueData
    {
        public BitmaskValueData(NativeIntegralType nativeBaseType, BitmaskValue bitmaskValue)
                throws ZserioExtensionException
        {
            name = bitmaskValue.getName();
            value = nativeBaseType.formatLiteral(bitmaskValue.getValue());
            isZero = bitmaskValue.getValue().equals(BigInteger.ZERO);
            final List<DocComment> valueDocComments = bitmaskValue.getDocComments();
            docComments = valueDocComments.isEmpty() ? null : new DocCommentsTemplateData(valueDocComments);
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

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String value;
        private final boolean isZero;
        private final DocCommentsTemplateData docComments;
    };

    private final NativeIntegralTypeInfoTemplateData underlyingTypeInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String upperBound;
    private final List<BitmaskValueData> values;
}
