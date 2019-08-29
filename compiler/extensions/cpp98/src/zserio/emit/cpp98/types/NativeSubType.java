package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeSubType extends CppNativeType
{
    public NativeSubType(PackageName packageName, String name, String includeFileName,
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
