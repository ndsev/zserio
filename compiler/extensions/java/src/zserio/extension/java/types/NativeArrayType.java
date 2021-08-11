package zserio.extension.java.types;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(NativeArrayableType elementType)
    {
        super(elementType.getPackageName(), elementType.getName() + "[]");

        arrayWrapper = new NativeArrayWrapper();
        rawArrayHolder = new NativeRawArrayHolder(elementType);
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

    public NativeRawArrayHolder getRawArrayHolder()
    {
        return rawArrayHolder;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayWrapper arrayWrapper;
    private final NativeRawArrayHolder rawArrayHolder;
    private final NativeArrayTraits arrayTraits;
}
