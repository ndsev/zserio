package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeIntegralType;

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

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final CppNativeType nativeEnumType = cppNativeMapper.getCppType(enumType);

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(enumTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        underlyingTypeInfo = new NativeIntegralTypeInfoTemplateData(nativeBaseType, enumTypeInstantiation);

        bitSize = BitSizeTemplateData.create(context, enumTypeInstantiation, this);
        runtimeFunction = RuntimeFunctionDataCreator.createData(context, enumTypeInstantiation, this);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(context, nativeBaseType, nativeEnumType, enumItem));
    }

    public boolean getUsedInPackedArray()
    {
        return usedInPackedArray;
    }

    public NativeIntegralTypeInfoTemplateData getUnderlyingTypeInfo()
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
        public EnumItemData(TemplateDataContext context, NativeIntegralType nativeBaseType,
                CppNativeType nativeEnumType, EnumItem enumItem) throws ZserioExtensionException
        {
            schemaName = enumItem.getName();
            name = AccessorNameFormatter.getEnumeratorName(enumItem);
            fullName = CppFullNameFormatter.getFullName(
                    nativeEnumType.getPackageName(), nativeEnumType.getName(), name);
            value = nativeBaseType.formatLiteral(enumItem.getValue());
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

        public String getFullName()
        {
            return fullName;
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
        private final String fullName;
        private final String value;
        private final boolean isDeprecated;
        private final boolean isRemoved;
        private final DocCommentsTemplateData docComments;
    };

    private final boolean usedInPackedArray;
    private final NativeIntegralTypeInfoTemplateData underlyingTypeInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
