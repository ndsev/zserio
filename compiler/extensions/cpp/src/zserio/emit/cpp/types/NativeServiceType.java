package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeServiceType extends CppNativeType
{
    public NativeServiceType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, false);
        addUserIncludeFile(includeFileName);
    }
}
