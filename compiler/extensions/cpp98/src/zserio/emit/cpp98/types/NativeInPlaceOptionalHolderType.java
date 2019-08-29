package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeInPlaceOptionalHolderType extends NativeOptionalHolderType
{
    public NativeInPlaceOptionalHolderType(PackageName packageName, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(packageName, zserioIncludePathRoot, wrappedType, IN_PLACE_OPTIONAL_HOLDER_TEMPLATE);
    }

    private final static String IN_PLACE_OPTIONAL_HOLDER_TEMPLATE = "InPlaceOptionalHolder";

}
