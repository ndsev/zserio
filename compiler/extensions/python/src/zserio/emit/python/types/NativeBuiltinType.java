package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeBuiltinType extends PythonNativeType
{
    public NativeBuiltinType(String builtinTypeName)
    {
        super(PackageName.EMPTY, builtinTypeName);
    }

    @Override
    public String getFullName()
    {
        return getName();
    }
}
