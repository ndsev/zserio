package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeCompoundType extends NativeUserType
{
    public NativeCompoundType(PackageName packageName, String name, String includeFileName)
    {
        super(packageName, name, includeFileName, false);
    }
}
