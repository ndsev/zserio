package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * FreeMarker template data for EnumerationEmitter.
 */
public class EnumerationEmitterTemplateData extends UserTypeTemplateData
{
    public EnumerationEmitterTemplateData(TemplateDataContext context, EnumType enumType)
            throws ZserioExtensionException
    {
        super(context, enumType, enumType.getDocComments());

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final CppNativeType nativeEnumType = cppNativeMapper.getCppType(enumType);

        final TypeInstantiation enumTypeInstantiation = enumType.getTypeInstantiation();
        final NativeIntegralType nativeBaseType = cppNativeMapper.getCppIntegralType(enumTypeInstantiation);
        addHeaderIncludesForType(nativeBaseType);

        underlyingTypeInfo = new NativeIntegralTypeInfoTemplateData(nativeBaseType, enumTypeInstantiation);

        bitSize = BitSizeTemplateData.create(context, enumTypeInstantiation, this);
        runtimeFunction = CppRuntimeFunctionDataCreator.createData(context, enumTypeInstantiation, this);

        final List<EnumItem> enumItems = enumType.getItems();
        items = new ArrayList<EnumItemData>(enumItems.size());
        for (EnumItem enumItem : enumItems)
            items.add(new EnumItemData(nativeBaseType, nativeEnumType, enumItem));
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

    public static class EnumItemData
    {
        public EnumItemData(NativeIntegralType nativeBaseType, CppNativeType nativeEnumType, EnumItem enumItem)
                throws ZserioExtensionException
        {
            name = enumItem.getName();
            fullName = CppFullNameFormatter.getFullName(nativeEnumType.getPackageName(),
                    nativeEnumType.getName(), enumItem.getName());
            value = nativeBaseType.formatLiteral(enumItem.getValue());
            final List<DocComment> itemDocComments = enumItem.getDocComments();
            docComments = itemDocComments.isEmpty() ? null : new DocCommentsTemplateData(itemDocComments);
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

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String fullName;
        private final String value;
        private final DocCommentsTemplateData docComments;
    };

    private final NativeIntegralTypeInfoTemplateData underlyingTypeInfo;
    private final BitSizeTemplateData bitSize;
    private final RuntimeFunctionTemplateData runtimeFunction;
    private final List<EnumItemData> items;
}
