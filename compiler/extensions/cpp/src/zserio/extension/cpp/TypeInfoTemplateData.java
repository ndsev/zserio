package zserio.extension.cpp;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class TypeInfoTemplateData
{
    public TypeInfoTemplateData(TypeReference typeReference, CppNativeType cppNativeType)
            throws ZserioExtensionException
    {
        cppTypeName = cppNativeType.getFullName();
        final ZserioType baseType = typeReference.getBaseTypeReference().getType();
        final boolean hasTypeInfo = baseType instanceof CompoundType ||
                baseType instanceof EnumType || baseType instanceof BitmaskType;
        typeInfoGetter = hasTypeInfo ? null : mapTypeInfoGetter(typeReference);
        isDynamicBitField = baseType instanceof DynamicBitFieldType;
        isEnum = baseType instanceof EnumType;
    }

    public TypeInfoTemplateData(CompoundType compoundType, CppNativeType cppNativeType)
    {
        cppTypeName = cppNativeType.getFullName();
        typeInfoGetter = null;
        isDynamicBitField = false;
        isEnum = false;
    }

    public String getCppTypeName()
    {
        return cppTypeName;
    }

    public String getTypeInfoGetter()
    {
        return typeInfoGetter;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    public boolean getIsEnum()
    {
        return isEnum;
    }

    private static String mapTypeInfoGetter(TypeReference typeReference) throws ZserioExtensionException
    {
        final RuntimeFunctionTemplateData runtimeFunction =
                CppRuntimeFunctionDataCreator.createTypeInfoData(typeReference);
        return "get" + runtimeFunction.getSuffix() + "(" +
                (runtimeFunction.getArg() != null ? runtimeFunction.getArg() : "")  + ")";
    }

    private final String cppTypeName;
    private final String typeInfoGetter;
    private final boolean isDynamicBitField;
    private final boolean isEnum;
}
