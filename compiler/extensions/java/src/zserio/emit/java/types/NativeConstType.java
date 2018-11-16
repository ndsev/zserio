package zserio.emit.java.types;

import zserio.ast.PackageName;
import zserio.emit.java.JavaFullNameFormatter;

public class NativeConstType extends JavaNativeType
{
    public NativeConstType(PackageName packageName, String name, JavaNativeType targetType)
    {
        super(packageName, name);
        this.targetType = targetType;
    }

    @Override
    public String getFullName()
    {
        return JavaFullNameFormatter.getFullName(getPackageName(), CONST_CLASS_NAME, getName());
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public JavaNativeType getTargetType()
    {
        return targetType;
    }

    public static String getClassName()
    {
        return CONST_CLASS_NAME;
    }

    private final JavaNativeType targetType;

    private static final String CONST_CLASS_NAME = "__ConstType";
}
