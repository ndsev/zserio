package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeSetType extends CppNativeType
{
    public NativeSetType(TypesContext typesContext)
    {
        super(typesContext.getSet().getPackage(), typesContext.getSet().getName());
        needsAllocatorArgument = typesContext.getSet().needsAllocatorArgument();

        if (needsAllocatorArgument)
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getSet().getSystemInclude());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    private final boolean needsAllocatorArgument;
}
