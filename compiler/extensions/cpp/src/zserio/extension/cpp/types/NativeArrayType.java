package zserio.extension.cpp.types;

/**
 * Native C++ array type mapping.
 */
public final class NativeArrayType extends NativeRuntimeType
{
    public NativeArrayType(CppNativeArrayableType elementType, NativeRuntimeAllocType nativeVectorType)
    {
        super("Array", "zserio/Array.h");

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
