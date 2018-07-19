package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.BitFieldType;
import zserio.ast.ConstType;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
import zserio.ast.StdIntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.cpp.types.CppNativeType;
import zserio.emit.cpp.types.NativeIntegralType;

public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
    {
        super(context, enumType);

        // add const type includes
        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        for (ConstType constType : enumType.getReferencedConstTypes())
        {
            final CppNativeType nativeType = cppNativeTypeMapper.getCppType(constType);
            addCppIncludesForType(nativeType);
        }

        final IntegerType enumBaseType = enumType.getIntegerBaseType();
        final NativeIntegralType nativeBaseType = cppNativeTypeMapper.getCppIntegralType(enumBaseType);
        addHeaderIncludesForType(nativeBaseType);

        baseCppTypeName = nativeBaseType.getFullName();
        zserioTypeName = ZserioTypeUtil.getFullName(enumType);

        bitSize = createBitSize(enumType);
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(enumBaseType, cppExpressionFormatter);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(nativeBaseType, enumType, enumItem));
    }

    public String getBaseCppTypeName()
    {
        return baseCppTypeName;
    }

    public String getZserioTypeName()
    {
        return zserioTypeName;
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

    public static class EnumItemData
    {
        public EnumItemData(NativeIntegralType nativeBaseType, EnumType enumType, EnumItem enumItem)
        {
            name = enumItem.getName();
            value = nativeBaseType.formatLiteral(enumItem.getValue());
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
    };

    private static String createBitSize(EnumType enumType)
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

        return (bitSize != null) ? CppLiteralFormatter.formatUInt8Literal(bitSize) : null;
    }

    private final String baseCppTypeName;
    private final String zserioTypeName;
    private final String bitSize;

    private final RuntimeFunctionTemplateData   runtimeFunction;
    private final List<EnumItemData>            items;
}
