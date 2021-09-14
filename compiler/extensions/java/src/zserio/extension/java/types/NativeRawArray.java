package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeRawArray extends JavaNativeType
{
    public NativeRawArray(String rawArrayName)
    {
        super(RUNTIME_ARRAY_PACKAGE, RAW_ARRAY_NAME + "." + rawArrayName);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public boolean requiresElementClass()
    {
        return false;
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
    private static final String RAW_ARRAY_NAME = "RawArray";
}
