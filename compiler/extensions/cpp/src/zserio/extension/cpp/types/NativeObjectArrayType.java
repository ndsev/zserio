package zserio.extension.cpp.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(CppNativeType elementType)
    {
        super(elementType, "ObjectArrayTraits", true);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
