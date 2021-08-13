package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeDoubleType extends NativeArrayableType
{
    public NativeDoubleType(boolean nullable)
    {
        super(nullable ? JAVA_LANG_PACKAGE : PackageName.EMPTY, nullable ? "Double" : "double",
                new NativeArrayTraits("Float64ArrayTraits"));

        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
