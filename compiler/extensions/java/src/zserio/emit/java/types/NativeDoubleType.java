package zserio.emit.java.types;

import zserio.ast.PackageName;

public class NativeDoubleType extends JavaNativeType
{
    public NativeDoubleType(boolean nullable)
    {
        super(PackageName.EMPTY, nullable ? "Double" : "double");
        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
