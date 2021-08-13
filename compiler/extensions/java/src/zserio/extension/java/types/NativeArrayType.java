package zserio.extension.java.types;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(NativeArrayableType elementType)
    {
        super(elementType.getPackageName(), elementType.getName() + "[]");

        arrayWrapper = new NativeArrayWrapper();
        rawArray = new NativeRawArray(elementType);
        arrayTraits = elementType.getArrayTraits();
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public NativeArrayWrapper getArrayWrapper()
    {
        return arrayWrapper;
    }

    public NativeRawArray getRawArray()
    {
        return rawArray;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayWrapper arrayWrapper;
    private final NativeRawArray rawArray;
    private final NativeArrayTraits arrayTraits;
}
