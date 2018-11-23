package zserio.emit.python.types;

import zserio.ast.PackageName;
import zserio.emit.common.NativeType;
import zserio.emit.python.PythonFullNameFormatter;

public class PythonNativeType implements NativeType
{
    protected PythonNativeType(PackageName packageName, String name)
    {
        this.packageName = packageName;
        this.name = name;
    }

    @Override
    public String getFullName()
    {
        return PythonFullNameFormatter.getFullName(this.packageName, name);
    }

    @Override
    public String getName()
    {
        return name;
    }

    public PackageName getPackageName()
    {
        return packageName;
    }

    private final PackageName packageName;
    private final String name;
}
