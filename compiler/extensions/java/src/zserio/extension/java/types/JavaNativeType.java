package zserio.extension.java.types;

import zserio.ast.PackageName;
import zserio.extension.common.NativeType;
import zserio.extension.java.JavaFullNameFormatter;

public abstract class JavaNativeType implements NativeType
{
    public JavaNativeType(PackageName packageName, String name)
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
    public PackageName getPackageName()
    {
        return packageName;
    }

    public abstract boolean isSimple();

    protected static final PackageName JAVA_LANG_PACKAGE =
            new PackageName.Builder().addId("java").addId("lang").get();

    private final PackageName packageName;
    private final String name;
}
