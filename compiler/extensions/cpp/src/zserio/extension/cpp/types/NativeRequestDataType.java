package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeRequestDataType extends CppNativeType
{
    public NativeRequestDataType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getRequestData().getPackage(),
                typesContext.getRequestData().getName() +
                (typesContext.getRequestData().isTemplate()
                        ? "<" + (typesContext.getRequestData().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getRequestData().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getRequestData().getSystemInclude());
    }
}
