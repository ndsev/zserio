package zserio.extension.cpp.types;

import zserio.extension.cpp.TypesContext;
import zserio.extension.cpp.TypesContext.NativeTypeDefinition;

/**
 * Native C++ runtime arrayable type mapping for types which depend on allocators.
 */
public final class NativeRuntimeAllocArrayableType extends NativeRuntimeAllocType implements CppNativeArrayableType
{
    public NativeRuntimeAllocArrayableType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, NativeIntegralType allocatorTemplateArg,
            NativeTypeDefinition nativeArrayTraitsDefinition)
    {
        this(nativeTypeDefinition, allocatorDefinition, allocatorTemplateArg.getFullName(),
                nativeArrayTraitsDefinition);
    }

    public NativeRuntimeAllocArrayableType(TypesContext.NativeTypeDefinition nativeTypeDefinition,
            TypesContext.AllocatorDefinition allocatorDefinition, String allocatorTemplateArgName,
            NativeTypeDefinition nativeArrayTraitsDefinition)
    {
        super(nativeTypeDefinition, allocatorDefinition, allocatorTemplateArgName);

        arrayTraits = new NativeArrayTraits(nativeArrayTraitsDefinition.getPackage(),
                getName(nativeArrayTraitsDefinition, allocatorDefinition, ""),
                nativeArrayTraitsDefinition.getSystemInclude());
        addIncludeFiles(arrayTraits);
    }

    @Override
    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private NativeArrayTraits arrayTraits;
}
