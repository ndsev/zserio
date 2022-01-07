package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java SQL database type mapping.
 */
public class NativeSqlDatabaseType extends JavaNativeType
{
    public NativeSqlDatabaseType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
