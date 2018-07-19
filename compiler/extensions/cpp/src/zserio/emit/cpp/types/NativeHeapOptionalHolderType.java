package zserio.emit.cpp.types;

import java.util.List;

public class NativeHeapOptionalHolderType extends NativeOptionalHolderType
{
    public NativeHeapOptionalHolderType(List<String> zserioNamespace, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(zserioNamespace, zserioIncludePathRoot, wrappedType, HEAP_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String HEAP_OPTIONAL_HOLDER_TEMPLATE = "HeapOptionalHolder";
}
