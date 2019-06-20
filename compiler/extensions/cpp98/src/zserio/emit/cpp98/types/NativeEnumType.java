package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeEnumType extends CppNativeType
{
    public NativeEnumType(PackageName packageName, String name, String includeFileName,
            NativeIntegralType baseType)
    {
        super(packageName, name, true);
        this.baseType = baseType;
        addUserIncludeFile(includeFileName);
    }

    public NativeIntegralType getBaseType()
    {
        return baseType;
    }

    private final NativeIntegralType baseType;
}
