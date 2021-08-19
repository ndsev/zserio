package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativePackingContextNodeType extends CppNativeType
{
    public NativePackingContextNodeType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getPackingContextNode().getPackage(),
                typesContext.getPackingContextNode().getName() +
                (typesContext.getPackingContextNode().isTemplate()
                        ? "<" + (typesContext.getPackingContextNode().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getPackingContextNode().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getPackingContextNode().getSystemInclude());
    }
}
