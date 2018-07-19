package zserio.emit.cpp.types;

import java.util.List;

import zserio.emit.cpp.CppUtil;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(List<String> zserioNamespace, String includePathRoot,
            CppNativeType elementType)
    {
        super(zserioNamespace,
                CppUtil.formatTemplateInstantiation(OBJECT_ARRAY_TEMPLATE, elementType.getFullName()),
                includePathRoot + OBJECT_ARRAY_INCLUDE, elementType);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }

    private final static String OBJECT_ARRAY_TEMPLATE = "ObjectArray";
    private final static String OBJECT_ARRAY_INCLUDE = "ObjectArray.h";
}
