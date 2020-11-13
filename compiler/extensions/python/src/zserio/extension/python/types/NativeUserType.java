package zserio.extension.python.types;

import zserio.ast.PackageName;

public class NativeUserType extends PythonNativeType
{
    public NativeUserType(PackageName packageName, String name)
    {
        super(packageName, name);
    }
}
