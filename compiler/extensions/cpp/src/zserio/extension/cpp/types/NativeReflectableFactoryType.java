package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeReflectableFactoryType extends CppNativeType
{
    public NativeReflectableFactoryType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getRelectableFactory().getPackage(),
                typesContext.getRelectableFactory().getName() +
                (typesContext.getRelectableFactory().isTemplate()
                        ? "<" + (typesContext.getRelectableFactory().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getRelectableFactory().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getRelectableFactory().getSystemInclude());
    }
}
