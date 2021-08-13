package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeSqlTableType extends JavaNativeType
{
    public NativeSqlTableType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
