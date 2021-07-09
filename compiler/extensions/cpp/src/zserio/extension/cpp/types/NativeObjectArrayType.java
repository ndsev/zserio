package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(CppNativeType elementType, TypesContext typesContext,
            NativeVectorType vectorType)
    {
        super(elementType, "ObjectArrayTraits", true, typesContext, vectorType);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
