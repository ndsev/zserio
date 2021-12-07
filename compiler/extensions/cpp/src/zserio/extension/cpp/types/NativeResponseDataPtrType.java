package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeResponseDataPtrType extends CppNativeType
{
    public NativeResponseDataPtrType(TypesContext typesContext, NativeIntegralType uint8Type)
    {
        super(typesContext.getResponseDataPtr().getPackage(),
                typesContext.getResponseDataPtr().getName() +
                (typesContext.getResponseDataPtr().isTemplate()
                        ? "<" + (typesContext.getResponseDataPtr().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() +
                                        "<" + uint8Type.getFullName() + ">>"
                                : ">")
                        : ""));

        if (typesContext.getResponseDataPtr().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getResponseDataPtr().getSystemInclude());
    }
}
