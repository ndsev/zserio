package zserio.extension.cpp.types;

import zserio.ast.PackageName;
import zserio.extension.cpp.TypesContext;

/**
 * Native C++ runtime type mapping for types which depend on allocators.
 */
public class NativeRuntimeAllocType extends NativeRuntimeType
{
    public NativeRuntimeAllocType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, NativeIntegralType allocatorTemplateArg)
    {
        this(nativeTypeDefinition, allocatorDefinition, nativeTypeDefinition.getPackage(),
                getName(nativeTypeDefinition, allocatorDefinition, allocatorTemplateArg.getFullName()));
    }

    public NativeRuntimeAllocType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, String allocatorTemplateArgName)
    {
        this(nativeTypeDefinition, allocatorDefinition, nativeTypeDefinition.getPackage(),
                getName(nativeTypeDefinition, allocatorDefinition, allocatorTemplateArgName));
    }

    public NativeRuntimeAllocType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition)
    {
        this(nativeTypeDefinition, allocatorDefinition, nativeTypeDefinition.getPackage(),
                nativeTypeDefinition.getName());
    }

    public boolean needsAllocatorArgument()
    {
        return needsAllocatorArgument;
    }

    protected static String getName(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, String allocatorTemplateArgName)
    {
        String name = nativeTypeDefinition.getName();
        if (nativeTypeDefinition.isTemplate())
        {
            name += "<" +
                    getAllocatorArgument(nativeTypeDefinition, allocatorDefinition, allocatorTemplateArgName) +
                    ">";
        }

        return name;
    }

    private static String getAllocatorArgument(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, String allocatorTemplateArgName)
    {
        if (!nativeTypeDefinition.needsAllocatorArgument())
            return "";

        String allocatorArgument = allocatorDefinition.getAllocatorType();
        if (!allocatorTemplateArgName.isEmpty())
            allocatorArgument += "<" + allocatorTemplateArgName + ">";

        return allocatorArgument;
    }

    private NativeRuntimeAllocType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, PackageName packageName, String name)
    {
        super(packageName, name);

        needsAllocatorArgument = nativeTypeDefinition.needsAllocatorArgument();
        if (needsAllocatorArgument)
            addSystemIncludeFile(allocatorDefinition.getAllocatorSystemInclude());
        addSystemIncludeFile(nativeTypeDefinition.getSystemInclude());
    }

    private final boolean needsAllocatorArgument;
}
