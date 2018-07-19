package zserio.emit.cpp.types;

import java.util.List;

public class NativeConstType extends CppNativeType
{
    public NativeConstType(List<String> namespacePath, String name, String includeFileName,
            CppNativeType targetType)
    {
        super(namespacePath, name, targetType.isSimpleType());
        this.targetType = targetType;
        addUserIncludeFile(includeFileName);
        addIncludeFiles(targetType);
    }

    public CppNativeType getTargetType()
    {
        return targetType;
    }

    private final CppNativeType targetType;
}
