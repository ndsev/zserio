package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeReflectablePtrType extends CppNativeType
{
    public NativeReflectablePtrType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getReflectablePtr().getPackage(),
                typesContext.getReflectablePtr().getName() +
                (typesContext.getReflectablePtr().isTemplate()
                        ? "<" + (typesContext.getReflectablePtr().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getReflectablePtr().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getReflectablePtr().getSystemInclude());
    }
}
