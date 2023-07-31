package zserio.extension.cpp.types;

/**
 * Native C++ array type mapping.
 */
public class NativeArrayType extends NativeRuntimeType
{
    public NativeArrayType(String arrayName, CppNativeArrayableType elementType,
            NativeRuntimeAllocType nativeVectorType)
    {
        super(arrayName, "zserio/Array.h");

        this.arrayTraits = elementType.getArrayTraits();

        addIncludeFiles(nativeVectorType);
        addIncludeFiles(elementType);
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayTraits arrayTraits;
}
