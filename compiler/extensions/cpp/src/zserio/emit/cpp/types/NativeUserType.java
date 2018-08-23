package zserio.emit.cpp.types;

import java.util.List;

public class NativeUserType extends CppNativeType
{
    public NativeUserType(List<String> namespacePath, String name, String includeFileName)
    {
        super(namespacePath, name, false);
        addUserIncludeFile(includeFileName);
    }
}
