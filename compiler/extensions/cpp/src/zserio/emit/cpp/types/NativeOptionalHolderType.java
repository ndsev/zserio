package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeOptionalHolderType extends CppNativeType
{
    public NativeOptionalHolderType(CppNativeType wrappedType)
    {
        super(ZSERIO_PACKAGE_NAME, "OptionalHolder<" + wrappedType.getFullName() + ">", false);
        this.wrappedType = wrappedType;

        addSystemIncludeFile(OPTIONAL_HOLDER_INCLUDE);
        addIncludeFiles(wrappedType);
    }

    public CppNativeType getWrappedType()
    {
        return wrappedType;
    }

    private final CppNativeType wrappedType;

    private final static PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private final static String OPTIONAL_HOLDER_INCLUDE = "zserio/OptionalHolder.h";
}
