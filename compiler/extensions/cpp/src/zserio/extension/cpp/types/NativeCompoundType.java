package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ compound type mapping.
 */
public class NativeCompoundType extends NativeUserArrayableType
{
    public NativeCompoundType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, includeFileName, false,
                new NativeArrayTraits("ObjectArrayTraits", true, false, true));
    }
}
