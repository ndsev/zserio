package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeIntegralArrayType extends NativeArrayType
{
    public NativeIntegralArrayType(PackageName packageName, String name, String includeFileName,
            CppNativeType elementType)
    {
        super(packageName, name, includeFileName, elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
