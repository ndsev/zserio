package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.ast.BytesType;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumType;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.NativeBuiltinType;
import zserio.extension.python.types.NativeSubtype;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data with info about types.
 */
public final class NativeTypeInfoTemplateData
{
    public NativeTypeInfoTemplateData(PythonNativeType pythonNativeType, TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        this(pythonNativeType, typeInstantiation, typeInstantiation.getTypeReference());
    }

    public NativeTypeInfoTemplateData(PythonNativeType pythonNativeType, TypeReference typeReference)
            throws ZserioExtensionException
    {
        this(pythonNativeType, null, typeReference);
    }

    public String getTypeFullName()
    {
        return typeFullName;
    }

    public boolean getIsBuiltin()
    {
        return isBuiltin;
    }

    public boolean getIsBytes()
    {
        return isBytes;
    }

    public boolean getIsEnum()
    {
        return isEnum;
    }

    public boolean getIsBitmask()
    {
        return isBitmask;
    }

    public boolean getHasTypeInfo()
    {
        return hasTypeInfo;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    public ArrayTraitsTemplateData getArrayTraits()
    {
        return arrayTraits;
    }

    public String getSchemaTypeFullName()
    {
        return schemaTypeFullName;
    }

    public RuntimeFunctionTemplateData getHashCodeFunc()
    {
        return hashCodeFunc;
    }

    private NativeTypeInfoTemplateData(PythonNativeType pythonNativeType, TypeInstantiation typeInstantiation,
            TypeReference typeReference) throws ZserioExtensionException
    {
        typeFullName = PythonFullNameFormatter.getFullName(pythonNativeType);
        final PythonNativeType pythonNativeBaseType = (pythonNativeType instanceof NativeSubtype)
                ? ((NativeSubtype)pythonNativeType).getNativeTargetBaseType()
                : pythonNativeType;
        isBuiltin = pythonNativeBaseType instanceof NativeBuiltinType;
        final ZserioType baseType = typeReference.getBaseTypeReference().getType();
        isBytes = baseType instanceof BytesType;
        isEnum = baseType instanceof EnumType;
        isBitmask = baseType instanceof BitmaskType;
        final boolean isCompound = baseType instanceof CompoundType;
        hasTypeInfo = isCompound || isEnum || isBitmask;
        isDynamicBitField = baseType instanceof DynamicBitFieldType;
        arrayTraits = new ArrayTraitsTemplateData(pythonNativeType.getArrayTraits());
        schemaTypeFullName = ZserioTypeUtil.getFullName(typeReference.getType());
        if (baseType instanceof SqlTableType)
        {
            hashCodeFunc = null;
        }
        else
        {
            hashCodeFunc = (typeInstantiation != null)
                    ? RuntimeFunctionDataCreator.createHashCodeData(typeInstantiation)
                    : RuntimeFunctionDataCreator.createHashCodeData(typeReference);
        }
    }

    private final String typeFullName;
    private final boolean isBuiltin;
    private final boolean isBytes;
    private final boolean isEnum;
    private final boolean isBitmask;
    private final boolean hasTypeInfo;
    private final boolean isDynamicBitField;
    private final ArrayTraitsTemplateData arrayTraits;
    private final String schemaTypeFullName;
    private final RuntimeFunctionTemplateData hashCodeFunc;
}