package zserio.extension.java.types;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(NativeArrayableType elementType)
    {
        super(elementType.getPackageName(), elementType.getName() + "[]");

        arrayWrapper = new NativeArrayWrapper();
        rawArray = elementType.getRawArray();
        arrayTraits = elementType.getArrayTraits();
        arrayElement = elementType.getArrayElement();
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

    public NativeArrayElement getArrayElement()
    {
        return arrayElement;
    }

    private final NativeArrayWrapper arrayWrapper;
    private final NativeRawArray rawArray;
    private final NativeArrayTraits arrayTraits;
    private final NativeArrayElement arrayElement;
}
