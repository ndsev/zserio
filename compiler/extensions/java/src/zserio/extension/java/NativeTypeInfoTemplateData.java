package zserio.extension.java;

import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeArrayType;
import zserio.extension.java.types.NativeArrayableType;
import zserio.extension.java.types.NativeBitmaskType;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeBytesType;
import zserio.extension.java.types.NativeCompoundType;
import zserio.extension.java.types.NativeDoubleType;
import zserio.extension.java.types.NativeEnumType;
import zserio.extension.java.types.NativeFloatType;
import zserio.extension.java.types.NativeIntegralType;
import zserio.extension.java.types.NativeLongType;
import zserio.extension.java.types.NativeSqlDatabaseType;
import zserio.extension.java.types.NativeSqlTableType;

/**
 * FreeMarker template data with info about types.
 */
public final class NativeTypeInfoTemplateData
{
    public NativeTypeInfoTemplateData(JavaNativeType javaNativeType, TypeInstantiation typeInstantiation)
            throws ZserioExtensionException
    {
        this(javaNativeType, typeInstantiation, null);
    }

    public NativeTypeInfoTemplateData(JavaNativeType javaNativeType, TypeReference typeReference)
            throws ZserioExtensionException
    {
        this(javaNativeType, null, typeReference);
    }

    public NativeTypeInfoTemplateData(JavaNativeType javaNativeType) throws ZserioExtensionException
    {
        this(javaNativeType, null, null);
    }

    public String getTypeFullName()
    {
        return typeFullName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public boolean getIsSimple()
    {
        return isSimple;
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

    public boolean getIsBoolean()
    {
        return isBoolean;
    }

    public boolean getIsLong()
    {
        return isLong;
    }

    public boolean getIsFloat()
    {
        return isFloat;
    }

    public boolean getIsDouble()
    {
        return isDouble;
    }

    public boolean getIsIntegral()
    {
        return isIntegral;
    }

    public boolean getRequiresBigInt()
    {
        return requiresBigInt;
    }

    public String getRequiredCast()
    {
        return requiredCast;
    }

    public ArrayableInfoTemplateData getArrayableInfo()
    {
        return arrayableInfo;
    }

    public RuntimeFunctionTemplateData getTypeInfoGetter()
    {
        return typeInfoGetter;
    }

    private NativeTypeInfoTemplateData(JavaNativeType javaNativeType, TypeInstantiation typeInstantiation,
            TypeReference typeReference) throws ZserioExtensionException
    {
        typeFullName = javaNativeType.getFullName();
        typeName = javaNativeType.getName();
        isSimple = javaNativeType.isSimple();
        isBytes = javaNativeType instanceof NativeBytesType;

        isEnum = javaNativeType instanceof NativeEnumType;
        isBitmask = javaNativeType instanceof NativeBitmaskType;
        isBoolean = javaNativeType instanceof NativeBooleanType;
        isLong = javaNativeType instanceof NativeLongType;
        isFloat = javaNativeType instanceof NativeFloatType;
        isDouble = javaNativeType instanceof NativeDoubleType;
        isIntegral = javaNativeType instanceof NativeIntegralType;
        requiresBigInt = isIntegral ? ((NativeIntegralType)javaNativeType).requiresBigInt() : false;
        requiredCast = javaNativeType.requiredCast();

        if (javaNativeType instanceof NativeArrayableType)
            arrayableInfo = new ArrayableInfoTemplateData((NativeArrayableType)javaNativeType);
        else
            arrayableInfo = null;

        final boolean isCompound = javaNativeType instanceof NativeCompoundType;
        final boolean isSqlDatabase = javaNativeType instanceof NativeSqlDatabaseType;
        final boolean isSqlTable = javaNativeType instanceof NativeSqlTableType;
        final boolean isArray = javaNativeType instanceof NativeArrayType;
        final boolean hasTypeInfo = isCompound || isSqlDatabase || isSqlTable || isEnum || isBitmask;
        if (hasTypeInfo || isArray || (typeInstantiation == null && typeReference == null))
        {
            typeInfoGetter = null;
        }
        else
        {
            typeInfoGetter = (typeInstantiation != null)
                    ? RuntimeFunctionDataCreator.createTypeInfoData(typeInstantiation)
                    : RuntimeFunctionDataCreator.createTypeInfoData(typeReference);
        }
    }

    private final String typeFullName;
    private final String typeName;
    private final boolean isSimple;
    private final boolean isBytes;
    private final boolean isEnum;
    private final boolean isBitmask;
    private final boolean isBoolean;
    private final boolean isLong;
    private final boolean isFloat;
    private final boolean isDouble;
    private final boolean isIntegral;
    private final boolean requiresBigInt;
    private final String requiredCast;
    private final ArrayableInfoTemplateData arrayableInfo;
    private final RuntimeFunctionTemplateData typeInfoGetter;
}
