package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeHeapOptionalHolderType extends NativeOptionalHolderType
{
    public NativeHeapOptionalHolderType(PackageName packageName, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(packageName, zserioIncludePathRoot, wrappedType, HEAP_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String HEAP_OPTIONAL_HOLDER_TEMPLATE = "HeapOptionalHolder";
}
