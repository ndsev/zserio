package zserio.emit.cpp.types;

import zserio.ast.PackageName;
import zserio.emit.cpp.CppFullNameFormatter;

public class NativeConstType extends CppNativeType
{
    public NativeConstType(PackageName packageName, String name, String includePathRoot,
            CppNativeType targetType)
    {
        super(packageName, name, targetType.isSimpleType());
        this.targetType = targetType;
        addUserIncludeFile(includePathRoot + CONST_TYPE_INCLUDE);
        addIncludeFiles(targetType);
    }

    @Override
    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(getPackageName(), CONST_TYPE_NAME, getName());
    }

    public CppNativeType getTargetType()
    {
        return targetType;
    }

    private final CppNativeType targetType;
    private final static String CONST_TYPE_NAME = "ConstType";
    private final static String CONST_TYPE_INCLUDE = "ConstType.h";
}
