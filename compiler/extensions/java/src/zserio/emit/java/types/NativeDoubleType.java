package zserio.emit.java.types;

public class NativeDoubleType extends JavaNativeType
{
    public NativeDoubleType(boolean nullable)
    {
        super("", nullable ? "Double" : "double");
        this.nullable = nullable;
    }

    @Override
    public boolean isSimple()
    {
        return !nullable;
    }

    private final boolean nullable;
}
