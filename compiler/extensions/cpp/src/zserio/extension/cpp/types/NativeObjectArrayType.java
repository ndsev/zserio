package zserio.extension.cpp.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(CppNativeType elementType, NativeVectorType vectorType)
    {
        super(elementType, "ObjectArrayTraits", true, vectorType);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
