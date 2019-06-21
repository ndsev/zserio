package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeCompoundType extends CppNativeType
{
    public NativeCompoundType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, false);
        addUserIncludeFile(includeFileName);
    }
}
