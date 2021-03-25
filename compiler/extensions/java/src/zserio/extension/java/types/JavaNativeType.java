package zserio.extension.java.types;

import zserio.ast.PackageName;
import zserio.extension.java.JavaFullNameFormatter;
import zserio.extension.java.symbols.JavaNativeSymbol;

/**
 * Java native type - e.g. compound type, subtype, etc.
 */
public abstract class JavaNativeType extends JavaNativeSymbol
{
    public JavaNativeType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public String getFullName()
    {
        return JavaFullNameFormatter.getFullName(getPackageName(), getName());
    }

    public abstract boolean isSimple();

    protected static final PackageName JAVA_LANG_PACKAGE =
            new PackageName.Builder().addId("java").addId("lang").get();
}
