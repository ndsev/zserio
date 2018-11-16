package zserio.emit.java.types;

import zserio.ast.PackageName;

public class NativeServiceType extends JavaNativeType
{
    public NativeServiceType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
