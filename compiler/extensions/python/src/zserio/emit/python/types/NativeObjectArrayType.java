package zserio.emit.python.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(String traitsName)
    {
        super(traitsName, false, true);
    }
}
