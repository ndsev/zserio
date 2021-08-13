package zserio.extension.java.types;

import zserio.ast.PackageName;

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
