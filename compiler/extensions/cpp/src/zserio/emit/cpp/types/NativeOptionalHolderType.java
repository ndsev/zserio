package zserio.emit.cpp.types;

import java.util.List;

import zserio.emit.cpp.CppUtil;

public class NativeOptionalHolderType extends CppNativeType
{
    public NativeOptionalHolderType(List<String> zserioNamespace, String zserioIncludePathRoot,
            CppNativeType wrappedType, String optionalHolderTemplate)
    {
        super(zserioNamespace,
                CppUtil.formatTemplateInstantiation(optionalHolderTemplate, wrappedType.getFullName()), false);
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
