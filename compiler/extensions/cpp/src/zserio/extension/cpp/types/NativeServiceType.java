package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeServiceType extends CppNativeType
{
    public NativeServiceType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getService().getPackage(),
                typesContext.getService().getName() +
                (typesContext.getService().isTemplate()
                        ? "<" + (typesContext.getService().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getService().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getService().getSystemInclude());
    }
}
