package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeUserArrayableType extends NativeUserType implements CppNativeArrayableType
{
    public NativeUserArrayableType(PackageName packageName, String name, String includeFileName,
            boolean isSimpleType, NativeArrayTraits arrayTraits)
    {
        super(packageName, name, includeFileName, isSimpleType);

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
