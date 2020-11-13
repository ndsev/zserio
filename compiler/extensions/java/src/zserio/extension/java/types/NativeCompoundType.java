package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeCompoundType extends JavaNativeType
{
    public NativeCompoundType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
