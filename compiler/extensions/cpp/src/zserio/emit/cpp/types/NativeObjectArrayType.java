package zserio.emit.cpp.types;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(CppNativeType elementType)
    {
        super(elementType, "ObjectArrayTraits<" + elementType.getFullName() + ">", true);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
