package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeAnyHolderType extends CppNativeType
{
    public NativeAnyHolderType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getAnyHolder().getPackage(), typesContext.getAnyHolder().getName() +
                (typesContext.getAnyHolder().isTemplate() ?
                        "<" + (typesContext.getAnyHolder().needsAllocatorArgument() ?
                                typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>" : ">") : "")
        );

        if (typesContext.getAnyHolder().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getAnyHolder().getSystemInclude());
    }
}
