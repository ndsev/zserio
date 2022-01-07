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
        super(context, enumType);

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
        final NativeIntegralType nativeIntegralType =
                javaNativeMapper.getJavaIntegralType(enumTypeInstantiation);
        baseJavaTypeName = nativeIntegralType.getFullName();

        underlyingTypeInfo = new TypeInfoTemplateData(enumTypeInstantiation, nativeIntegralType);

        arrayableInfo = new ArrayableInfoTemplateData(nativeIntegralType);
        bitSize = BitSizeTemplateData.create(enumTypeInstantiation, javaExpressionFormatter);

        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(enumTypeInstantiation,
                javaExpressionFormatter, javaNativeMapper);

        items = new ArrayList<EnumItemData>();
        for (EnumItem item: enumType.getItems())
            items.add(new EnumItemData(javaNativeMapper, enumType, item));
    }

    public String getBaseJavaTypeName()
    {
        return baseJavaTypeName;
    }

    public TypeInfoTemplateData getUnderlyingTypeInfo()
    {
        return underlyingTypeInfo;
    }

    public ArrayableInfoTemplateData getArrayableInfo()
    {
        return arrayableInfo;
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

    public static class EnumItemData
    {
        public EnumItemData(JavaNativeMapper javaNativeMapper, EnumType enumType, EnumItem enumItem)
                throws ZserioExtensionException
        {
            name = enumItem.getName();

            final NativeIntegralType nativeIntegralType =
                    javaNativeMapper.getJavaIntegralType(enumType.getTypeInstantiation());
            value = nativeIntegralType.formatLiteral(enumItem.getValue());
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        private final String name;
        private final String value;
    }

    private final String baseJavaTypeName;
    private final TypeInfoTemplateData underlyingTypeInfo;
    private final ArrayableInfoTemplateData arrayableInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
