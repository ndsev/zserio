package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeUserArrayableType extends NativeArrayableType
{
    public NativeUserArrayableType(PackageName packageName, String name, String includeFileName,
            boolean isSimpleType, NativeArrayTraits arrayTraits)
    {
        super(packageName, name, isSimpleType, arrayTraits);
        addUserIncludeFile(includeFileName);
    }
}
