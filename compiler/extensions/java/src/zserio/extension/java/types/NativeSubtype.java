package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java subtype mapping.
 */
public final class NativeSubtype extends JavaNativeType
{
    public NativeSubtype(PackageName packageName, String name, boolean isSimple)
    {
        super(packageName, name);

        this.isSimple = isSimple;
    }

    @Override
    public boolean isSimple()
    {
        return isSimple;
    }

    private final boolean isSimple;
}
