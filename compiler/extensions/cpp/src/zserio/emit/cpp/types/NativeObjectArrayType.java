package zserio.emit.cpp.types;

import zserio.ast.PackageName;
import zserio.emit.cpp.TemplateInstantiationFormatter;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(PackageName packageName, String includePathRoot,
            CppNativeType elementType)
    {
        super(packageName,
                TemplateInstantiationFormatter.format(OBJECT_ARRAY_TEMPLATE, elementType.getFullName()),
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
