package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeOptimizedOptionalHolderType extends NativeOptionalHolderType
{
    public NativeOptimizedOptionalHolderType(PackageName packageName, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(packageName, zserioIncludePathRoot, wrappedType, OPTIMIZED_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String OPTIMIZED_OPTIONAL_HOLDER_TEMPLATE = "OptimizedOptionalHolder";
}
