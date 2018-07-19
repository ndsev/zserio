package zserio.emit.cpp.types;

import java.util.List;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(List<String> namespacePath, String name, String includeFileName,
            CppNativeType elementType)
    {
        super(namespacePath, name, false);
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
