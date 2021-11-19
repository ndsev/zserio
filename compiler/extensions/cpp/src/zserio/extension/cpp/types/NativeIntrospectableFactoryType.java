package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeIntrospectableFactoryType extends CppNativeType
{
    public NativeIntrospectableFactoryType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getIntrospectableFactory().getPackage(),
                typesContext.getIntrospectableFactory().getName() +
                (typesContext.getIntrospectableFactory().isTemplate()
                        ? "<" + (typesContext.getIntrospectableFactory().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getIntrospectableFactory().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getIntrospectableFactory().getSystemInclude());
    }
}
