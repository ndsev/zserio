package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for TypeInfo.
 */
public class TypeInfoTemplateData
{
    public TypeInfoTemplateData(TemplateDataContext context, TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        this(typeInstantiation.getTypeReference(),
                context.getPythonNativeMapper().getPythonType(typeInstantiation));
    }

    public TypeInfoTemplateData(TemplateDataContext context, TypeReference typeReference)
            throws ZserioExtensionException
    {
        this(typeReference, context.getPythonNativeMapper().getPythonType(typeReference));
    }

    public TypeInfoTemplateData(TypeReference typeReference, PythonNativeType nativeType)
    {
        schemaTypeName = ZserioTypeUtil.getFullName(typeReference.getType());
        pythonTypeName = PythonFullNameFormatter.getFullName(nativeType);
        final ZserioType baseType = typeReference.getBaseTypeReference().getType();
        hasTypeInfo = baseType instanceof CompoundType ||
                baseType instanceof EnumType || baseType instanceof BitmaskType;
        isDynamicBitField = baseType instanceof DynamicBitFieldType;
    }

    public TypeInfoTemplateData(CompoundType compoundType, PythonNativeType nativeType)
    {
        schemaTypeName = ZserioTypeUtil.getFullName(compoundType);
        pythonTypeName = PythonFullNameFormatter.getFullName(nativeType);
        hasTypeInfo = true;
        isDynamicBitField = false;
    }

    public String getSchemaTypeName()
    {
        return schemaTypeName;
    }

    public String getPythonTypeName()
    {
        return pythonTypeName;
    }

    public boolean getHasTypeInfo()
    {
        return hasTypeInfo;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    private final String schemaTypeName;
    private final String pythonTypeName;
    private final boolean hasTypeInfo;
    private final boolean isDynamicBitField;
}