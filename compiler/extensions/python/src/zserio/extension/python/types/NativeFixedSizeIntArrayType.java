package zserio.extension.python.types;

public class NativeFixedSizeIntArrayType extends NativeArrayType
{
    public NativeFixedSizeIntArrayType(String traitsName)
    {
        super(traitsName, true, false);
    }
}
