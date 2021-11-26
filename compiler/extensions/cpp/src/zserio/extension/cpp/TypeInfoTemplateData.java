package zserio.extension.cpp;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.EnumType;
import zserio.ast.IntegerType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

public class TypeInfoTemplateData
{
    public TypeInfoTemplateData(TypeReference typeReference, CppNativeType cppNativeType)
            throws ZserioExtensionException
    {
        this(typeReference, null, cppNativeType);
    }

    public TypeInfoTemplateData(TypeInstantiation typeInstantiation, CppNativeType cppNativeType)
            throws ZserioExtensionException
    {
        this(typeInstantiation.getTypeReference(), typeInstantiation, cppNativeType);
    }

    public TypeInfoTemplateData(CompoundType compoundType, CppNativeType cppNativeType)
    {
        cppTypeName = cppNativeType.getFullName();
        typeInfoGetter = null;
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

    private TypeInfoTemplateData(TypeReference typeReference, TypeInstantiation typeInstantiation,
            CppNativeType cppNativeType) throws ZserioExtensionException
    {
        cppTypeName = cppNativeType.getFullName();
        final ZserioType baseType = typeReference.getBaseTypeReference().getType();
        typeInfoGetter = createTypeInfoGetter(baseType, typeReference, typeInstantiation);
        isEnum = baseType instanceof EnumType;
        isBitmask = baseType instanceof BitmaskType;
        isSigned = baseType instanceof IntegerType && ((IntegerType)baseType).isSigned();
    }

    private RuntimeFunctionTemplateData createTypeInfoGetter(ZserioType baseType, TypeReference typeReference,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final boolean hasTypeInfo = baseType instanceof CompoundType ||
                baseType instanceof EnumType || baseType instanceof BitmaskType;
        if (hasTypeInfo)
            return null;

        return (typeInstantiation != null) ?
                CppRuntimeFunctionDataCreator.createTypeInfoData(typeInstantiation) :
                        CppRuntimeFunctionDataCreator.createTypeInfoData(typeReference);
    }

    private final String cppTypeName;
    private final RuntimeFunctionTemplateData typeInfoGetter;
    private final boolean isEnum;
    private final boolean isBitmask;
    private final boolean isSigned;
}
