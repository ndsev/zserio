package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitFieldType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
import zserio.ast.StdIntegerType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.NativeIntegralType;

/**
 * The enum data used for FreeMarker template during enum file generation.
 */
public final class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioEmitException
    {
        super(context, enumType);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final IntegerType enumBaseType = enumType.getIntegerBaseType();
        final NativeIntegralType nativeIntegralType = javaNativeMapper.getJavaIntegralType(enumBaseType);
        baseJavaTypeName = nativeIntegralType.getFullName();

        bitSize = createBitSize(enumType);

        runtimeFunction = JavaRuntimeFunctionDataCreator.createData(enumBaseType,
                context.getJavaExpressionFormatter(), javaNativeMapper);

        items = new ArrayList<EnumItemData>();
        for (EnumItem item: enumType.getItems())
            items.add(new EnumItemData(javaNativeMapper, enumType, item));
    }

    public String getBaseJavaTypeName()
    {
        return baseJavaTypeName;
    }

    public String getBitSize()
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

    private static String createBitSize(EnumType enumType) throws ZserioEmitException
    {
        final IntegerType integerBaseType = enumType.getIntegerBaseType();
        Integer bitSize = null;
        if (integerBaseType instanceof StdIntegerType)
        {
            bitSize = ((StdIntegerType)integerBaseType).getBitSize();
        }
        else if (integerBaseType instanceof BitFieldType)
        {
            bitSize = ((BitFieldType)integerBaseType).getBitSize();
        }

        return (bitSize != null) ? JavaLiteralFormatter.formatDecimalLiteral(bitSize) : null;
    }

    public static class EnumItemData
    {
        public EnumItemData(JavaNativeMapper javaNativeMapper, EnumType enumType, EnumItem enumItem)
                throws ZserioEmitException
        {
            name = enumItem.getName();

            final NativeIntegralType nativeIntegralType =
                    (NativeIntegralType)javaNativeMapper.getJavaType(enumType.getIntegerBaseType());
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
    private final String bitSize;

    private final RuntimeFunctionTemplateData   runtimeFunction;
    private final List<EnumItemData>            items;
}
