package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeInplaceOptionalHolderType extends CppNativeType
{
    public NativeInplaceOptionalHolderType()
    {
        super(ZSERIO_PACKAGE_NAME, "InplaceOptionalHolder");

        addSystemIncludeFile("zserio/OptionalHolder.h");
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
