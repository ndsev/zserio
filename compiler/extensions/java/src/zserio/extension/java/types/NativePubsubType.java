package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativePubsubType extends JavaNativeType
{
    public NativePubsubType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
