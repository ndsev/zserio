package zserio.extension.java;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.NativeIntegralType;

/**
 * FreeMarker template data for BitmaskEmitter.
 */
public final class BitmaskEmitterTemplateData extends UserTypeTemplateData
{
    public BitmaskEmitterTemplateData(TemplateDataContext context, BitmaskType bitmaskType)
            throws ZserioExtensionException
    {
        super(context, bitmaskType, bitmaskType);

        final TypeInstantiation bitmaskTypeInstantiation = bitmaskType.getTypeInstantiation();
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final ExpressionFormatter javaLambdaExpressionFormatter = context.getJavaLambdaExpressionFormatter();
        final NativeIntegralType nativeIntegralType =
                javaNativeMapper.getJavaIntegralType(bitmaskTypeInstantiation);

        underlyingTypeInfo = new NativeTypeInfoTemplateData(nativeIntegralType, bitmaskTypeInstantiation);
        bitSize = BitSizeTemplateData.create(bitmaskTypeInstantiation, javaExpressionFormatter,
                javaLambdaExpressionFormatter);
        runtimeFunction = RuntimeFunctionDataCreator.createData(context, bitmaskTypeInstantiation);

        final BigInteger lowerBound = getLowerBound(bitmaskTypeInstantiation);
        this.lowerBound = lowerBound.equals(nativeIntegralType.getLowerBound()) ? null :
                nativeIntegralType.formatLiteral(lowerBound);
        // upper bound is needed for negation since java uses signed types!
        final BigInteger upperBound = getUpperBound(bitmaskTypeInstantiation);
        this.upperBound = nativeIntegralType.formatLiteral(upperBound);
        this.checkUpperBound = !upperBound.equals(nativeIntegralType.getUpperBound());

        values = new ArrayList<BitmaskValueData>();
        for (BitmaskValue bitmaskValue: bitmaskType.getValues())
            values.add(new BitmaskValueData(context, nativeIntegralType, bitmaskValue));
    }

    public NativeTypeInfoTemplateData getUnderlyingTypeInfo()
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

    private static BigInteger getUpperBound(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getUpperBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getUpperBound();
        throw new ZserioExtensionException("Unexpected bitmask type instantiation!");
    }

    private static BigInteger getLowerBound(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final ZserioType baseType = typeInstantiation.getBaseType();

        if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            return ((DynamicBitFieldInstantiation)typeInstantiation).getLowerBound();
        else if (baseType instanceof IntegerType)
            return ((IntegerType)baseType).getLowerBound();
        throw new ZserioExtensionException("Unexpected bitmask type instantiation!");
    }

    public static class BitmaskValueData
    {
        public BitmaskValueData(TemplateDataContext context, NativeIntegralType nativeBaseType,
                BitmaskValue bitmaskValue) throws ZserioExtensionException
        {
            name = bitmaskValue.getName();
            value = nativeBaseType.formatLiteral(bitmaskValue.getValue());
            isZero = bitmaskValue.getValue().equals(BigInteger.ZERO);
            docComments = DocCommentsDataCreator.createData(context, bitmaskValue);
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
    }

    private final NativeTypeInfoTemplateData underlyingTypeInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final String lowerBound;
    private final String upperBound;
    private final boolean checkUpperBound;
    private final List<BitmaskValueData> values;
}
