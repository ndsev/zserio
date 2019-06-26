package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(CppNativeType elementType)
    {
        super(STD_PACKAGE_NAME, "vector<" + elementType.getFullName() + ">");
        this.elementType = elementType;
        addSystemIncludeFile("vector");
        addIncludeFiles(elementType);
    }

    public CppNativeType getElementType()
    {
        return elementType;
    }

    public boolean requiresElementFactory()
    {
        return false;
    }

    private static final PackageName STD_PACKAGE_NAME = new PackageName.Builder().addId("std").get();

    private final CppNativeType elementType;
}
