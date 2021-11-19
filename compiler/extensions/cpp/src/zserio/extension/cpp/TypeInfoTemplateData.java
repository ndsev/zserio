package zserio.extension.cpp;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
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
        typeInfoGetter = hasTypeInfo ? null : CppRuntimeFunctionDataCreator.createTypeInfoData(typeReference);
        isDynamicBitField = baseType instanceof DynamicBitFieldType;
        isEnum = baseType instanceof EnumType;
        isBitmask = baseType instanceof BitmaskType;
        isSigned = baseType instanceof IntegerType && ((IntegerType)baseType).isSigned();
    }

    public TypeInfoTemplateData(CompoundType compoundType, CppNativeType cppNativeType)
    {
        cppTypeName = cppNativeType.getFullName();
        typeInfoGetter = null;
        isDynamicBitField = false;
        isEnum = false;
        isBitmask = false;
        isSigned = false;
    }

    public String getCppTypeName()
    {
        return cppTypeName;
    }

    public RuntimeFunctionTemplateData getTypeInfoGetter()
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

    public boolean getIsBitmask()
    {
        return isBitmask;
    }

    public boolean getIsSigned()
    {
        return isSigned;
    }

    private final String cppTypeName;
    private final RuntimeFunctionTemplateData typeInfoGetter;
    private final boolean isDynamicBitField;
    private final boolean isEnum;
    private final boolean isBitmask;
    private final boolean isSigned;
}
