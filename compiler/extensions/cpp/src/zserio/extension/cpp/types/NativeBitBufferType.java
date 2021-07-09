package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeBitBufferType extends CppNativeType
{
    public NativeBitBufferType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getBitBuffer().getPackage(), typesContext.getBitBuffer().getName() +
                (typesContext.getBitBuffer().isTemplate() ?
                        "<" + (typesContext.getBitBuffer().needsAllocatorArgument() ?
                                typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>" : ">") : "")
        );

        if (typesContext.getBitBuffer().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getBitBuffer().getSystemInclude());
    }

}
