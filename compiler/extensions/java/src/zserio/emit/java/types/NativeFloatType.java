package zserio.emit.java.types;

import zserio.ast.PackageName;

public class NativeFloatType extends JavaNativeType
{
    public NativeFloatType(boolean nullable)
    {
        super(PackageName.EMPTY, nullable ? "Float" : "float");
        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
