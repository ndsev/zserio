package zserio.emit.cpp.types;

import java.util.List;

public class NativeCompoundType extends CppNativeType
{
    public NativeCompoundType(List<String> namespacePath, String name, String includeFileName)
    {
        super(namespacePath, name, false);
        addUserIncludeFile(includeFileName);
    }
}
