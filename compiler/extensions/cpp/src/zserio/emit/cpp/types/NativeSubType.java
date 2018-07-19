package zserio.emit.cpp.types;

import java.util.List;

public class NativeSubType extends CppNativeType
{
    public NativeSubType(List<String> namespacePath, String name, String includeFileName,
            CppNativeType targetType)
    {
        super(namespacePath, name, targetType.isSimpleType());
        this.targetType = targetType;
        addUserIncludeFile(includeFileName);
    }

    public CppNativeType getTargetType()
    {
        return targetType;
    }

    private final CppNativeType targetType;
}
