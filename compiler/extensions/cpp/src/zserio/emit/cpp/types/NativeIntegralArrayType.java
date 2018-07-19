package zserio.emit.cpp.types;

import java.util.List;

public class NativeIntegralArrayType extends NativeArrayType
{
    public NativeIntegralArrayType(List<String> namespacePath, String name, String includeFileName,
            CppNativeType elementType)
    {
        super(namespacePath, name, includeFileName, elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
