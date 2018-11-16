package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeConstType extends PythonNativeType
{
    public NativeConstType(PackageName packageName, String name)
    {
        super(packageName, name);
    }
}
