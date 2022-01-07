package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java compound type mapping.
 */
public class NativeCompoundType extends NativeArrayableType
{
    public NativeCompoundType(PackageName packageName, String name, boolean withWriterCode)
    {
        super(packageName, name,
                new NativeObjectRawArray(),
                new NativeObjectArrayTraits(packageName, name, withWriterCode),
                new NativeObjectArrayElement(packageName, name));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
