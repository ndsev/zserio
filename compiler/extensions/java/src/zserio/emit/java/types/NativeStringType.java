package zserio.emit.java.types;

import zserio.ast.PackageName;

public class NativeStringType extends JavaNativeType
{
    public NativeStringType()
    {
        super(PackageName.EMPTY, "String");
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
