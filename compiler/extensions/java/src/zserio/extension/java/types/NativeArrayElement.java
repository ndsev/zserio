package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeArrayElement extends JavaNativeType
{
    public NativeArrayElement(String name)
    {
        super(RUNTIME_ARRAY_PACKAGE, ARRAY_ELEMENT_NAME + "." + name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
    private static final String ARRAY_ELEMENT_NAME = "ArrayElement";
}
