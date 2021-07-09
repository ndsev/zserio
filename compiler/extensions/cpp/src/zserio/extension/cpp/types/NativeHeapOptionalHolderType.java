package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeHeapOptionalHolderType extends CppNativeType
{
    public NativeHeapOptionalHolderType(TypesContext typesContext)
    {
        super(typesContext.getHeapOptionalHolder().getPackage(),
                typesContext.getHeapOptionalHolder().getName());

        needsAllocatorArgument = typesContext.getHeapOptionalHolder().needsAllocatorArgument();

        if (needsAllocatorArgument)
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getHeapOptionalHolder().getSystemInclude());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    private final boolean needsAllocatorArgument;
}
