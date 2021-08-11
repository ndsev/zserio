package zserio.extension.java.types;

import java.util.Locale;

import zserio.ast.PackageName;

public class NativeRawArrayHolder extends JavaNativeType
{
    public NativeRawArrayHolder(JavaNativeType elementType)
    {
        super(RUNTIME_ARRAY_PACKAGE, getHolderName(elementType));

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

    private static String getHolderName(JavaNativeType elementType)
    {
        final String holderSubName = elementType.isSimple() ?
                getCapitalizeName(elementType.getName()) + "Array" : "ObjectArray<>";

        return RAW_ARRAY_HOLDER_NAME + "." + holderSubName;
    }

    private static String getCapitalizeName(String name)
    {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    private static final PackageName RUNTIME_ARRAY_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("array").get();
    private static final String RAW_ARRAY_HOLDER_NAME = "RawArrayHolder";

    private final boolean requiresElementClass;
}
