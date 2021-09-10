package zserio.extension.java.types;

public class NativeObjectRawArray extends NativeRawArray
{
    public NativeObjectRawArray()
    {
        super("ObjectRawArray<>");
    }

    public boolean requiresElementClass()
    {
        return true;
    }
}