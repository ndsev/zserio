package zserio.extension.cpp.types;

import java.util.Collections;

/**
 * Native C++ array type mapping.
 */
public final class NativeArrayType extends NativeRuntimeType
{
    public NativeArrayType(CppNativeArrayableType elementType, NativeRuntimeAllocType nativeVectorType)
    {
        super("Array", Collections.singleton("zserio/Array.h"));

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
