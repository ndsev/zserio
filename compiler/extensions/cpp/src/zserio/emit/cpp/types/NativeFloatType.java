package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeFloatType extends CppNativeType
{
    public NativeFloatType()
    {
        super(PackageName.EMPTY, "float");
    }
}
