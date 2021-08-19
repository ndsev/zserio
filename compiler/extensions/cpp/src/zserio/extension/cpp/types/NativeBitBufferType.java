package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;
import zserio.extension.cpp.TypesContext.NativeTypeDefinition;

public class NativeBitBufferType extends NativeArrayableType
{
    public NativeBitBufferType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getBitBuffer().getPackage(), typesContext.getBitBuffer().getName() +
                (typesContext.getBitBuffer().isTemplate()
                        ? "<" + (typesContext.getBitBuffer().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""),
                createBitBufferArrayTraits(typesContext)
        );

        if (typesContext.getBitBuffer().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getBitBuffer().getSystemInclude());
    }

    private static NativeArrayTraits createBitBufferArrayTraits(TypesContext typesContext)
    {
        final NativeTypeDefinition bitBufferArrayTraits = typesContext.getBitBufferArrayTraits();
        return new NativeArrayTraits(bitBufferArrayTraits.getPackage(), bitBufferArrayTraits.getName() +
                (bitBufferArrayTraits.isTemplate()
                        ? "<" + (bitBufferArrayTraits.needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() + ">"
                                : ">")
                        : ""),
                bitBufferArrayTraits.getSystemInclude(),
                false, // isTemplated
                false, // requiresElementBitSize
                false // requiresElementFactory
        );
    }
}
