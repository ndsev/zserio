package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ user type mapping.
 */
public class NativeUserType extends NativeType
{
    public NativeUserType(PackageName packageName, String name, String includeFileName, boolean isSimpleType)
    {
        super(packageName, name, isSimpleType);
        addUserIncludeFile(includeFileName);
    }
}
