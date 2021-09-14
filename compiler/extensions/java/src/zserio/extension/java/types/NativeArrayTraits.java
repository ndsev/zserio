package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeArrayTraits extends JavaNativeType
{
    public NativeArrayTraits(String name)
    {
        super(RUNTIME_ARRAY_PACKAGE, ARRAY_TRAITS_NAME + "." + name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public boolean requiresElementBitSize()
    {
        return false;
    }

    public boolean requiresElementFactory()
    {
        return false;
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
    private static final String ARRAY_TRAITS_NAME = "ArrayTraits";
}
