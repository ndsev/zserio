package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeMapType extends CppNativeType
{
    public NativeMapType(TypesContext typesContext)
    {
        super(typesContext.getMap().getPackage(), typesContext.getMap().getName());
        needsAllocatorArgument = typesContext.getMap().needsAllocatorArgument();

        if (needsAllocatorArgument)
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getMap().getSystemInclude());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    private final boolean needsAllocatorArgument;
}
