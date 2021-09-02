package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeIntegralType;

public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioExtensionException
    {
        super(context, enumType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();

        final CppNativeType nativeEnumType = cppNativeMapper.getCppType(enumType);

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(enumTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        arrayTraits = new ArrayTraitsTemplateData(nativeBaseType.getArrayTraits());
        bitSize = createBitSize(enumTypeInstantiation);
        baseCppTypeName = nativeBaseType.getFullName();

        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(
                enumTypeInstantiation, cppExpressionFormatter);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(nativeBaseType, nativeEnumType, enumItem));
    }

    public ArrayTraitsTemplateData getArrayTraits()
    {
        return arrayTraits;
    }

    public String getBitSize()
    {
        return bitSize;
    }

    public String getBaseCppTypeName()
    {
        return baseCppTypeName;
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
        public EnumItemData(NativeIntegralType nativeBaseType, CppNativeType nativeEnumType, EnumItem enumItem)
                throws ZserioExtensionException
        {
            name = enumItem.getName();
            fullName = CppFullNameFormatter.getFullName(nativeEnumType.getPackageName(),
                    nativeEnumType.getName(), enumItem.getName());
            value = nativeBaseType.formatLiteral(enumItem.getValue());
        }

        public String getName()
        {
            return name;
        }

        public String getFullName()
        {
            return fullName;
        }

        public String getValue()
        {
            return value;
        }

        private final String name;
        private final String fullName;
        private final String value;
    };

    private static String createBitSize(TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            return CppLiteralFormatter.formatUInt8Literal(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return CppLiteralFormatter.formatUInt8Literal(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getMaxBitSize());
        }
        else
        {
            return null;
        }
    }

    private final ArrayTraitsTemplateData arrayTraits;
    private final String bitSize;
    private final String baseCppTypeName;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
