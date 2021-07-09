package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;

public class NativeStringType extends CppNativeType
{
    public NativeStringType(TypesContext typesContext)
    {
        super(typesContext.getString().getPackage(), typesContext.getString().getName() +
                (typesContext.getString().isTemplate() ?
                        "<" + (typesContext.getString().needsAllocatorArgument() ?
                                typesContext.getAllocatorDefinition().getAllocatorType() + "<char>>" : ">") : "")
        );

        if (typesContext.getString().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getString().getSystemInclude());
    }
}
