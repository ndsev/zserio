package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeBuiltinType extends NativeArrayableType
{
    public NativeBuiltinType(String builtinTypeName, NativeArrayTraits arrayTraits)
    {
        super(PackageName.EMPTY, builtinTypeName, arrayTraits);
    }
}
