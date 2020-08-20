package zserio.emit.python.types;

import zserio.ast.PackageName;
import zserio.emit.common.NativeType;
import zserio.emit.python.PythonFullNameFormatter;

public class PythonNativeType implements NativeType
{
    protected PythonNativeType(String name)
    {
        fullName = name;
        packageName = PackageName.EMPTY;
        this.name = name;
    }

    protected PythonNativeType(PackageName packageName, String name)
    {
        fullName = PythonFullNameFormatter.getFullName(packageName, name);
        this.name = name;
        this.packageName = packageName;
    }

    protected PythonNativeType(PackageName packageName, String moduleName, String name)
    {
        fullName = PythonFullNameFormatter.getFullName(packageName, moduleName, name);
        this.name = name;
        this.packageName = packageName;
    }

    @Override
    public String getFullName()
    {
        return fullName;
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

    private final String fullName;
    private final String name;
    private final PackageName packageName;
}
