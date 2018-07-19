package zserio.emit.java.types;

public class NativeFloatType extends JavaNativeType
{
    public NativeFloatType(boolean nullable)
    {
        super("", nullable ? "Float" : "float");
        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
