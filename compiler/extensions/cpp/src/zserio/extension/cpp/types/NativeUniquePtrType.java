package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeUniquePtrType extends CppNativeType
{
    public NativeUniquePtrType(TypesContext typesContext)
    {
        super(typesContext.getUniquePtr().getPackage(),
                typesContext.getUniquePtr().getName());

        needsAllocatorArgument = typesContext.getUniquePtr().needsAllocatorArgument();

        if (needsAllocatorArgument)
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getUniquePtr().getSystemInclude());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    private final boolean needsAllocatorArgument;
}
