package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeVectorType extends CppNativeType
{
    public NativeVectorType(TypesContext typesContext)
    {
        super(typesContext.getVector().getPackage(), typesContext.getVector().getName());
        needsAllocatorArgument = typesContext.getVector().needsAllocatorArgument();

        if (needsAllocatorArgument)
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getVector().getSystemInclude());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    private final boolean needsAllocatorArgument;
}
