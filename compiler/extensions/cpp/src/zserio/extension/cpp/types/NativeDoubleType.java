package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeDoubleType extends CppNativeType
{
    public NativeDoubleType()
    {
        super(PackageName.EMPTY, "double");
    }
}
