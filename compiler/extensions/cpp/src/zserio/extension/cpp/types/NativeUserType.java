package zserio.extension.cpp.types;

import java.util.Collection;
import java.util.Collections;

import zserio.ast.PackageName;

/**
 * Native C++ user type mapping.
 */
public class NativeUserType extends NativeType
{
    public NativeUserType(PackageName packageName, String name, String includeFileName, boolean isSimpleType)
    {
        super(packageName, name, isSimpleType, null, Collections.singleton(includeFileName));
    }

    public NativeUserType(PackageName packageName, String name, boolean isSimpleType,
            Collection<String> systemIncludes, Collection<String> userIncludes)
    {
        super(packageName, name, isSimpleType, systemIncludes, userIncludes);
    }
}
