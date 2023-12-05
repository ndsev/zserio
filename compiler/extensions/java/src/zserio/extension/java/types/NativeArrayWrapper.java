package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java array wrapper mapping.
 */
public final class NativeArrayWrapper extends JavaNativeType
{
    public NativeArrayWrapper()
    {
        super(RUNTIME_ARRAY_PACKAGE, "Array");
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
}
