package zserio.extension.cpp.types;

public class NativeBitFieldArrayType extends NativeArrayType
{
    public NativeBitFieldArrayType(CppNativeType elementType)
    {
        super(elementType, "BitFieldArrayTraits", true);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
