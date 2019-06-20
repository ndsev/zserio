package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(PackageName packageName, String name, String includeFileName,
            CppNativeType elementType)
    {
        super(packageName, name, false);
        this.elementType = elementType;
        addSystemIncludeFile(includeFileName);
        addIncludeFiles(elementType);
    }

    public CppNativeType getElementType()
    {
        return elementType;
    }

    public boolean requiresElementBitSize()
    {
        return false;
    }

    public boolean requiresElementFactory()
    {
        return false;
    }

    private final CppNativeType elementType;
}
