package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(String name)
    {
        super(RUNTIME_ARRAY_PACKAGE, name);
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
}
