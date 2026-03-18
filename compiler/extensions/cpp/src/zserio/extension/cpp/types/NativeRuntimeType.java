package zserio.extension.cpp.types;

import java.util.Collection;
import java.util.Collections;

import zserio.ast.PackageName;

/**
 * Native C++ runtime type mapping.
 */
public class NativeRuntimeType extends NativeType
{
    public NativeRuntimeType(String name, Collection<String> systemIncludes)
    {
        this(name, systemIncludes, false);
    }

    public NativeRuntimeType(String name, Collection<String> systemIncludes, boolean isSimple)
    {
        this(ZSERIO_PACKAGE_NAME, name, systemIncludes, isSimple);
    }

    public NativeRuntimeType(PackageName packageName, String name, Collection<String> systemIncludes)
    {
        super(packageName, name, false, systemIncludes, null);
    }

    public NativeRuntimeType(
            PackageName packageName, String name, Collection<String> systemIncludes, boolean isSimple)
    {
        super(packageName, name, isSimple, systemIncludes, null);
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
