package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeBlobBufferType extends CppNativeType
{
    public NativeBlobBufferType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getBlobBuffer().getPackage(), typesContext.getBlobBuffer().getName() +
                (typesContext.getBlobBuffer().isTemplate() ?
                        "<" + (typesContext.getBlobBuffer().needsAllocatorArgument() ?
                                typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>" : ">") : "")
        );

        if (typesContext.getBlobBuffer().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getBlobBuffer().getSystemInclude());
    }

}
