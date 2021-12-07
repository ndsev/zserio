package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeServiceClientType extends CppNativeType
{
    public NativeServiceClientType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getServiceClient().getPackage(),
                typesContext.getServiceClient().getName() +
                (typesContext.getServiceClient().isTemplate()
                        ? "<" + (typesContext.getServiceClient().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getServiceClient().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getServiceClient().getSystemInclude());
    }
}
