package zserio.extension.java.types;

import java.util.Locale;

import zserio.ast.PackageName;

public class NativeRawArray extends JavaNativeType
{
    public NativeRawArray(JavaNativeType elementType)
    {
        super(RUNTIME_ARRAY_PACKAGE, getRawArrayName(elementType));

        requiresElementClass = !elementType.isSimple();
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public boolean requiresElementClass()
    {
        return requiresElementClass;
    }

    private static String getRawArrayName(JavaNativeType elementType)
    {
        final String rawArraySubName = elementType.isSimple() ?
                getCapitalizeName(elementType.getName()) + "RawArray" : "ObjectRawArray<>";

        return RAW_ARRAY_NAME + "." + rawArraySubName;
    }

    private static String getCapitalizeName(String name)
    {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
    private static final String RAW_ARRAY_NAME = "RawArray";

    private final boolean requiresElementClass;
}
