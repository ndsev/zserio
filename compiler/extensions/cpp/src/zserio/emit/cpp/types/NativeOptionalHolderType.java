package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeOptionalHolderType extends CppNativeType
{
    public NativeOptionalHolderType(PackageName packageName, String zserioIncludePathRoot,
            CppNativeType wrappedType)
    {
        super(packageName, "OptionalHolder<" + wrappedType.getFullName() + ">", false);
        this.wrappedType = wrappedType;

        addSystemIncludeFile(zserioIncludePathRoot + OPTIONAL_HOLDER_INCLUDE);
        addIncludeFiles(wrappedType);
    }

    public CppNativeType getWrappedType()
    {
        return wrappedType;
    }

    private final CppNativeType wrappedType;

    private final static String OPTIONAL_HOLDER_INCLUDE = "OptionalHolder.h";
}
