package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeCompoundType extends NativeArrayableType
{
    public NativeCompoundType(PackageName packageName, String name, boolean withWriterCode)
    {
        super(packageName, name, new NativeObjectArrayTraits(packageName, name, withWriterCode));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
