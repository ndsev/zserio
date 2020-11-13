package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeBooleanType extends CppNativeType
{
    public NativeBooleanType()
    {
        super(PackageName.EMPTY, "bool");
    }
}
