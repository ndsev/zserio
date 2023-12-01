package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.NativeIntegralType;

/**
 * FreeMarker template data for EnumerationEmitter.
 */
public final class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioExtensionException
    {
        super(context, enumType, enumType);

        usedInPackedArray = context.getPackedTypesCollector().isUsedInPackedArray(enumType);

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final ExpressionFormatter javaLambdaExpressionFormatter = context.getJavaLambdaExpressionFormatter();
        final NativeIntegralType nativeIntegralType =
                javaNativeMapper.getJavaIntegralType(enumTypeInstantiation);

        underlyingTypeInfo = new NativeTypeInfoTemplateData(nativeIntegralType, enumTypeInstantiation);
        bitSize = BitSizeTemplateData.create(enumTypeInstantiation, javaExpressionFormatter,
                javaLambdaExpressionFormatter);
        runtimeFunction = RuntimeFunctionDataCreator.createData(context, enumTypeInstantiation);

        items = new ArrayList<EnumItemData>();
        for (EnumItem item: enumType.getItems())
            items.add(new EnumItemData(context, nativeIntegralType, item));
    }

    public boolean getUsedInPackedArray()
    {
        return usedInPackedArray;
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

    public Iterable<EnumItemData> getItems()
    {
        return items;
    }

    public static final class EnumItemData
    {
        public EnumItemData(TemplateDataContext context, NativeIntegralType nativeIntegralType,
                EnumItem enumItem) throws ZserioExtensionException
        {
            schemaName = enumItem.getName();
            name = AccessorNameFormatter.getEnumeratorName(enumItem);
            value = nativeIntegralType.formatLiteral(enumItem.getValue());
            isDeprecated = enumItem.isDeprecated();
            isRemoved = enumItem.isRemoved();
            docComments = DocCommentsDataCreator.createData(context, enumItem);
        }

        public String getSchemaName()
        {
            return schemaName;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public boolean getIsDeprecated()
        {
            return isDeprecated;
        }

        public boolean getIsRemoved()
        {
            return isRemoved;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String schemaName;
        private final String name;
        private final String value;
        private final boolean isDeprecated;
        private final boolean isRemoved;
        private final DocCommentsTemplateData docComments;
    }

    private final boolean usedInPackedArray;
    private final NativeTypeInfoTemplateData underlyingTypeInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
