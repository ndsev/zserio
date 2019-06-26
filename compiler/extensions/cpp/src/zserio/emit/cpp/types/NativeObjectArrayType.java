package zserio.emit.cpp.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(CppNativeType elementType)
    {
        super(elementType);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
