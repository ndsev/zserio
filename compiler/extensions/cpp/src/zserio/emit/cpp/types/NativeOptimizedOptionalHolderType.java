package zserio.emit.cpp.types;

import java.util.List;

public class NativeOptimizedOptionalHolderType extends NativeOptionalHolderType
{
    public NativeOptimizedOptionalHolderType(List<String> zserioNamespace, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(zserioNamespace, zserioIncludePathRoot, wrappedType, OPTIMIZED_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String OPTIMIZED_OPTIONAL_HOLDER_TEMPLATE = "OptimizedOptionalHolder";
}
