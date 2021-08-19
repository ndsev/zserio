package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeArrayTraits extends CppNativeType
{
    public NativeArrayTraits(String name)
    {
        this(name, false);
    }

    public NativeArrayTraits(String name, boolean isTemplatable)
    {
        this(name, isTemplatable, false, false);
    }

    public NativeArrayTraits(String name, boolean isTemplated, boolean requiresElementBitSize,
            boolean requiresElementFactory)
    {
        this(ZSERIO_PACKAGE_NAME, name, ARRAY_TRAITS_INCLUDE, isTemplated,
                requiresElementBitSize, requiresElementFactory);
    }

    public NativeArrayTraits(PackageName packageName, String name, String includePath, boolean isTemplated,
            boolean requiresElementBitSize, boolean requiresElementFactory)
    {
        super(packageName, name);
        this.isTemplated = isTemplated;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementFactory = requiresElementFactory;

        addSystemIncludeFile(includePath);
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

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final String ARRAY_TRAITS_INCLUDE = "zserio/ArrayTraits.h";

    private final boolean isTemplated;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementFactory;
};
