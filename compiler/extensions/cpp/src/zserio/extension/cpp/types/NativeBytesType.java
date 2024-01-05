package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public final class NativeBytesType extends NativeRuntimeType implements CppNativeArrayableType
{
    public NativeBytesType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getVector().getPackage(), getName(typesContext, uint8Type),
                typesContext.getVector().getSystemInclude(), false);

        if (typesContext.getVector().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());

        final TypesContext.NativeTypeDefinition bytesArrayTraits = typesContext.getBytesArrayTraits();

        final StringBuilder bytesArrayTraitsName = new StringBuilder(bytesArrayTraits.getName());
        if (bytesArrayTraits.isTemplate())
        {
            bytesArrayTraitsName.append('<');
            bytesArrayTraitsName.append(typesContext.getAllocatorDefinition().getAllocatorType());
            bytesArrayTraitsName.append('>');
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        }
        arrayTraits = new NativeArrayTraits(bytesArrayTraits.getPackage(), bytesArrayTraitsName.toString(),
                bytesArrayTraits.getSystemInclude());
        addIncludeFiles(arrayTraits);
    }

    @Override
    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private static String getName(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        final StringBuilder name = new StringBuilder(typesContext.getVector().getName());
        name.append('<');
        name.append(uint8Type.getFullName());
        if (typesContext.getVector().needsAllocatorArgument())
        {
            name.append(',');
            name.append(typesContext.getAllocatorDefinition().getAllocatorType());
            name.append('<');
            name.append(uint8Type.getFullName());
            name.append('>');
        }
        name.append('>');

        return name.toString();
    }

    private final NativeArrayTraits arrayTraits;
};
