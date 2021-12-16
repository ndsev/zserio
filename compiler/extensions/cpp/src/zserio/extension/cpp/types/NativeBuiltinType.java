package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeBuiltinType extends NativeType implements CppNativeArrayableType
{
    public NativeBuiltinType(String builtinTypeName, NativeArrayTraits arrayTraits)
    {
        super(PackageName.EMPTY, builtinTypeName);

        this.arrayTraits = arrayTraits;
        addIncludeFiles(arrayTraits);
    }

    @Override
    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private NativeArrayTraits arrayTraits;
}
