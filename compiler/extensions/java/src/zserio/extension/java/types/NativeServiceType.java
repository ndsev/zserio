package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java service type mapping.
 */
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
