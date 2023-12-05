package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ compound type mapping.
 */
public final class NativeCompoundType extends NativeUserArrayableType
{
    public NativeCompoundType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, includeFileName, false,
                new NativeArrayTraits("ObjectArrayTraits", NativeArrayTraits.TYPE.REQUIRES_ELEMENT_FACTORY));
    }
}
