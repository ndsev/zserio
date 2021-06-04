package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for EnumerationEmitter.
 */
public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioExtensionException
    {
        super(context, enumType);

        importPackage("enum");
        importPackage("typing");
        importPackage("zserio");

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeType = pythonNativeMapper.getPythonType(enumTypeInstantiation);

        arrayTraits = new ArrayTraitsTemplateData(nativeType.getArrayTraits());
        bitSize = createBitSize(enumTypeInstantiation);

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        runtimeFunction = PythonRuntimeFunctionDataCreator.createData(
                enumTypeInstantiation, pythonExpressionFormatter);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(enumItem));
    }

    public ArrayTraitsTemplateData getArrayTraits()
    {
        return arrayTraits;
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
        public EnumItemData(EnumItem enumItem) throws ZserioExtensionException
        {
            name = PythonSymbolConverter.enumItemToSymbol(enumItem.getName());
            value = PythonLiteralFormatter.formatDecimalLiteral(enumItem.getValue());
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

    private static String createBitSize(TypeInstantiation typeInstantiation)
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            return PythonLiteralFormatter.formatDecimalLiteral(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            return PythonLiteralFormatter.formatDecimalLiteral(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getMaxBitSize());
        }
        else
        {
            return null;
        }
    }

    private final ArrayTraitsTemplateData arrayTraits;
    private final String bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
