package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeInstantiateType extends CppNativeType
{
    public NativeInstantiateType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, false);
        addUserIncludeFile(includeFileName);
    }
}
