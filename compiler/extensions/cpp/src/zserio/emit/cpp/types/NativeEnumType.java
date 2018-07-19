package zserio.emit.cpp.types;

import java.util.List;

public class NativeEnumType extends CppNativeType
{
    public NativeEnumType(List<String> namespacePath, String name, String includeFileName,
            NativeIntegralType baseType)
    {
        super(namespacePath, name, true);
        this.baseType = baseType;
        addUserIncludeFile(includeFileName);
    }

    public NativeIntegralType getBaseType()
    {
        return baseType;
    }

    private final NativeIntegralType baseType;
}
