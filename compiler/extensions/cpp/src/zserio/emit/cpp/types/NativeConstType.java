package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeConstType extends CppNativeType
{
    public NativeConstType(PackageName packageName, String name, String includeFileName,
            CppNativeType targetType)
    {
        super(packageName, name, targetType.isSimpleType());
        this.targetType = targetType;
        addUserIncludeFile(includeFileName);
    }

    public CppNativeType getTargetType()
    {
        return targetType;
    }

    private final CppNativeType targetType;
}
