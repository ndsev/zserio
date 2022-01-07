package zserio.extension.java;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.EnumType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for TypeInfo.
 */
public class TypeInfoTemplateData
{
    public TypeInfoTemplateData(TypeReference typeReference, JavaNativeType javaNativeType)
            throws ZserioExtensionException
    {
        this(typeReference, null, javaNativeType);
    }

    public TypeInfoTemplateData(TypeInstantiation typeInstantiation, JavaNativeType javaNativeType)
            throws ZserioExtensionException
    {
        this(typeInstantiation.getTypeReference(), typeInstantiation, javaNativeType);
    }

    public TypeInfoTemplateData(CompoundType compoundType, JavaNativeType javaNativeType)
    {
        javaTypeName = javaNativeType.getFullName();
        typeInfoGetter = null;
    }

    public String getJavaTypeName()
    {
        return javaTypeName;
    }

    public RuntimeFunctionTemplateData getTypeInfoGetter()
    {
        return typeInfoGetter;
    }

    private TypeInfoTemplateData(TypeReference typeReference, TypeInstantiation typeInstantiation,
            JavaNativeType javaNativeType) throws ZserioExtensionException
    {
        javaTypeName = javaNativeType.getFullName();
        final ZserioType baseType = typeReference.getBaseTypeReference().getType();
        typeInfoGetter = createTypeInfoGetter(baseType, typeReference, typeInstantiation);
    }

    private RuntimeFunctionTemplateData createTypeInfoGetter(ZserioType baseType, TypeReference typeReference,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        final boolean hasTypeInfo = baseType instanceof CompoundType ||
                baseType instanceof EnumType || baseType instanceof BitmaskType;
        if (hasTypeInfo)
            return null;

        return (typeInstantiation != null) ?
                JavaRuntimeFunctionDataCreator.createTypeInfoData(typeInstantiation) :
                        JavaRuntimeFunctionDataCreator.createTypeInfoData(typeReference);
    }

    private final String javaTypeName;
    private final RuntimeFunctionTemplateData typeInfoGetter;
}
