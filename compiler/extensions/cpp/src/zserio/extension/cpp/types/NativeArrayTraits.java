package zserio.extension.cpp.types;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;

public class NativeArrayTraits
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
        this.name = CppFullNameFormatter.getFullName(ZSERIO_PACKAGE_NAME, name);
        this.isTemplated = isTemplated;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementFactory = requiresElementFactory;
    }

    public String getName()
    {
        return name;
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

    private final String name;
    private final boolean isTemplated;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementFactory;
};