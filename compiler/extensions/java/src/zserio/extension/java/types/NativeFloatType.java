package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java float type mapping.
 */
public class NativeFloatType extends NativeArrayableType
{
    public NativeFloatType(boolean nullable, NativeArrayTraits arrayTraits)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Float" : "float",
                new NativeRawArray("FloatRawArray"), arrayTraits, new NativeArrayElement("FloatArrayElement"));

        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
