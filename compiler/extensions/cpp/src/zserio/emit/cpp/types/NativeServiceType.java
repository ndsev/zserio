package zserio.emit.cpp.types;

import java.util.List;

public class NativeServiceType extends CppNativeType
{
    public NativeServiceType(List<String> namespacePath, String name, String includeFileName)
    {
        super(namespacePath, name, false);
        addUserIncludeFile(includeFileName);
    }
}
