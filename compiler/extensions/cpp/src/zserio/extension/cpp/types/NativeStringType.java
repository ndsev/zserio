package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;
import zserio.extension.cpp.TypesContext.NativeTypeDefinition;

public class NativeStringType extends NativeArrayableType
{
    public NativeStringType(TypesContext typesContext)
    {
        super(typesContext.getString().getPackage(), typesContext.getString().getName() +
                (typesContext.getString().isTemplate()
                        ? "<" + (typesContext.getString().needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() + "<char>>"
                                : ">")
                        : ""),
                createStringArrayTraits(typesContext)
        );

        if (typesContext.getString().needsAllocatorArgument())
            addSystemIncludeFile(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
        addSystemIncludeFile(typesContext.getString().getSystemInclude());
    }

    private static NativeArrayTraits createStringArrayTraits(TypesContext typesContext)
    {
        final NativeTypeDefinition stringArrayTraits = typesContext.getStringArrayTraits();
        return new NativeArrayTraits(stringArrayTraits.getPackage(), stringArrayTraits.getName() +
                (stringArrayTraits.isTemplate()
                        ? "<" + (stringArrayTraits.needsAllocatorArgument()
                                ? typesContext.getAllocatorDefinition().getAllocatorType() + ">"
                                : ">")
                        : ""),
                stringArrayTraits.getSystemInclude(),
                false, // isTemplated
                false, // requiresElementBitSize
                false // requiresElementFactory
        );
    }
}
