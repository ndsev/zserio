package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java string type mapping.
 */
public class NativeStringType extends NativeArrayableType
{
    public NativeStringType()
    {
        super(JAVA_LANG_PACKAGE, "String",
                new NativeRawArray("StringRawArray"),
                new NativeArrayTraits("StringArrayTraits"),
                new NativeObjectArrayElement(STRING_PACKAGE, "String"));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private static final PackageName STRING_PACKAGE =
            new PackageName.Builder().addId("java").addId("lang").get();
}
