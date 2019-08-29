package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.NativeIntegralType;

public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioEmitException
    {
        super(context, enumType);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();

        final IntegerType enumBaseType = enumType.getIntegerBaseType();
        final NativeIntegralType nativeBaseType = cppNativeTypeMapper.getCppIntegralType(enumBaseType);
        addHeaderIncludesForType(nativeBaseType);

        baseCppTypeName = nativeBaseType.getFullName();

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
                throws ZserioEmitException
        {
            name = enumItem.getName();
            fullName = CppFullNameFormatter.getFullName(enumType.getPackage().getPackageName(),
                    enumType.getName(), enumItem.getName());
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

    private final String baseCppTypeName;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
