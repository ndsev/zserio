package zserio.emit.cpp.types;

import java.util.List;

public class NativeInPlaceOptionalHolderType extends NativeOptionalHolderType
{
    public NativeInPlaceOptionalHolderType(List<String> zserioNamespace, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(zserioNamespace, zserioIncludePathRoot, wrappedType, IN_PLACE_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String IN_PLACE_OPTIONAL_HOLDER_TEMPLATE = "InPlaceOptionalHolder";

}
