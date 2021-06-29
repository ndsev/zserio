package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeBitFieldArrayType extends NativeArrayType
{
    public NativeBitFieldArrayType(CppNativeType elementType, TypesContext typesContext,
            NativeVectorType vectorType)
    {
        super(elementType, "BitFieldArrayTraits", true, typesContext, vectorType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
