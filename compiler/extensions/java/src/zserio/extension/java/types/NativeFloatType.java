package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeFloatType extends NativeArrayableType
{
    public NativeFloatType(boolean nullable, NativeArrayTraits arrayTraits)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Float" : "float", arrayTraits);

        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
