package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeUserType extends CppNativeType
{
    public NativeUserType(PackageName packageName, String name, String includeFileName, boolean isSimpleType)
    {
        super(packageName, name, isSimpleType);
        addUserIncludeFile(includeFileName);
    }
}
