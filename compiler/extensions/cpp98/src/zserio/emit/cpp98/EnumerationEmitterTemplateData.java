package zserio.emit.cpp98;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioTypeUtil;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.NativeIntegralType;

public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioEmitException
    {
        super(context, enumType);

        // add const type includes
        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(enumTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        baseCppTypeName = nativeBaseType.getFullName();
        zserioTypeName = ZserioTypeUtil.getFullName(enumType);

        bitSize = createBitSize(enumType);
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(
                enumTypeInstantiation, cppExpressionFormatter);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(nativeBaseType, enumItem));
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
        public EnumItemData(NativeIntegralType nativeBaseType, EnumItem enumItem)
                throws ZserioEmitException
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

    private static String createBitSize(EnumType enumType) throws ZserioEmitException
    {
        final TypeInstantiation instantiation = enumType.getTypeInstantiation();
        final IntegerType integerBaseType = enumType.getIntegerBaseType();
        Integer bitSize = null;
        if (integerBaseType instanceof FixedSizeType)
        {
            bitSize = ((FixedSizeType)integerBaseType).getBitSize();
        }
        else if (instantiation instanceof DynamicBitFieldInstantiation)
        {
            bitSize = ((DynamicBitFieldInstantiation)instantiation).getMaxBitSize();
        }

        return (bitSize != null) ? CppLiteralFormatter.formatUInt8Literal(bitSize) : null;
    }

    private final String baseCppTypeName;
    private final String zserioTypeName;
    private final String bitSize;

    private final RuntimeFunctionTemplateData   runtimeFunction;
    private final List<EnumItemData>            items;
}
