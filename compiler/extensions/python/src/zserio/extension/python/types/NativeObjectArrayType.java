package zserio.extension.python.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(String traitsName)
    {
        super(traitsName, false, true);
    }
}
