package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ array traits mapping.
 */
public class NativeArrayTraits extends NativeRuntimeType
{
    public NativeArrayTraits(String name)
    {
        this(name, false);
    }

    public NativeArrayTraits(String name, boolean isTemplated)
    {
        this(name, isTemplated, false, false);
    }

    public NativeArrayTraits(String name, boolean isTemplated,
            boolean requiresElementBitSize, boolean requiresElementFactory)
    {
        super(name, "zserio/ArrayTraits.h");

        this.isTemplated = isTemplated;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementFactory = requiresElementFactory;
    }

    public NativeArrayTraits(PackageName packageName, String name, String systemIncludeFile,
            boolean isTemplated, boolean requiresElementBitSize, boolean requiresElementFactory)
    {
        super(packageName, name, systemIncludeFile);

        this.isTemplated = isTemplated;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementFactory = requiresElementFactory;
    }

    public boolean isTemplated()
    {
        return isTemplated;
    }

    public boolean requiresElementBitSize()
    {
        return requiresElementBitSize;
    }

    public boolean requiresElementFactory()
    {
        return requiresElementFactory;
    }

    private final boolean isTemplated;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementFactory;
};
