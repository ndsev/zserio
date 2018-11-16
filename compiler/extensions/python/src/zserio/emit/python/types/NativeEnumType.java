package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeEnumType extends PythonNativeType
{
    public NativeEnumType(PackageName packageName, String name)
    {
        super(packageName, name);
    }
}
