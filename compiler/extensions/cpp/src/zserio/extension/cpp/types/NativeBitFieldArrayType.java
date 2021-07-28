package zserio.extension.cpp.types;

public class NativeBitFieldArrayType extends NativeArrayType
{
    public NativeBitFieldArrayType(CppNativeType elementType, NativeVectorType vectorType)
    {
        super(elementType, "BitFieldArrayTraits", true, vectorType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
