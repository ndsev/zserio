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
        super(context, enumType, enumType);

        importPackage("typing");
        importPackage("zserio");

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final PythonNativeType nativeType = pythonNativeMapper.getPythonType(enumTypeInstantiation);
        if (getWithTypeInfoCode())
            importType(nativeType);

        underlyingTypeInfo = new NativeTypeInfoTemplateData(nativeType, enumTypeInstantiation);
        bitSize = createBitSize(enumTypeInstantiation);

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        runtimeFunction = RuntimeFunctionDataCreator.createData(
                enumTypeInstantiation, pythonExpressionFormatter);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(context, enumItem));
    }

    public NativeTypeInfoTemplateData getUnderlyingTypeInfo()
    {
        return underlyingTypeInfo;
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
        public EnumItemData(TemplateDataContext context, EnumItem enumItem) throws ZserioExtensionException
        {
            schemaName = enumItem.getName();
            name = PythonSymbolConverter.enumItemToSymbol(enumItem.getName(), enumItem.isRemoved());
            value = PythonLiteralFormatter.formatDecimalLiteral(enumItem.getValue());
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

    private final NativeTypeInfoTemplateData underlyingTypeInfo;
    private final String bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
