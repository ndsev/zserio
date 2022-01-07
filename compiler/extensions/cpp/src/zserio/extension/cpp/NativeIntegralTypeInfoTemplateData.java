package zserio.extension.cpp;

import zserio.ast.TypeInstantiation;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * FreeMarker template data with info about integral types.
 */
public class NativeIntegralTypeInfoTemplateData extends NativeTypeInfoTemplateData
{
    public NativeIntegralTypeInfoTemplateData(NativeIntegralType nativeBaseType,
            TypeInstantiation typeInstantiation) throws ZserioExtensionException
    {
        super(nativeBaseType, typeInstantiation);

        typeNumBits = nativeBaseType.getNumBits();
        isSigned = nativeBaseType.isSigned();
    }

    public int getTypeNumBits()
    {
        return typeNumBits;
    }

    public boolean getIsSigned()
    {
        return isSigned;
    }

    private final int typeNumBits;
    private final boolean isSigned;
}
