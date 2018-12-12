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

    public JavaNativeType getTargetType()
    {
        return targetType;
    }

    @Override
    public String getFullName()
    {
        return JavaFullNameFormatter.getFullName(getPackageName(), getName(), getName());
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private final JavaNativeType targetType;
}
