package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeIntrospectablePtrType extends CppNativeType
{
    public NativeIntrospectablePtrType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getIntrospectablePtr().getPackage(),
                typesContext.getIntrospectablePtr().getName() +
                (typesContext.getIntrospectablePtr().isTemplate()
                        ? "<" + (typesContext.getIntrospectablePtr().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getIntrospectablePtr().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getIntrospectablePtr().getSystemInclude());
    }
}
