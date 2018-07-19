package zserio.emit.java.types;

import zserio.emit.common.NativeType;
import zserio.emit.java.JavaFullNameFormatter;

public abstract class JavaNativeType implements NativeType
{
    public JavaNativeType(String packageName, String name)
    {
        this.packageName = packageName;
        this.name = name;
    }

    @Override
    public String getFullName()
    {
        return JavaFullNameFormatter.getFullName(getPackageName(), getName());
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Return the name of the package that contains this type.
     */
    public String getPackageName()
    {
        return packageName;
    }

    public abstract boolean isSimple();

    private final String packageName;
    private final String name;
}
