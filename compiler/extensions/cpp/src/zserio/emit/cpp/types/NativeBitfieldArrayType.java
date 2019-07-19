package zserio.emit.cpp.types;

public class NativeBitfieldArrayType extends NativeArrayType
{
    public NativeBitfieldArrayType(CppNativeType elementType)
    {
        super(elementType, "BitFieldArrayTraits", true);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
