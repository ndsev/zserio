package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeRuntimeType extends NativeType
{
    public NativeRuntimeType(PackageName packageName, String name)
    {
        super(packageName, name);
    }

    public NativeRuntimeType(String name, String systemIncludeFile)
    {
        this(name, systemIncludeFile, false);
    }

    public NativeRuntimeType(String name, String systemIncludeFile, boolean isSimple)
    {
        this(ZSERIO_PACKAGE_NAME, name, systemIncludeFile, isSimple);
    }

    public NativeRuntimeType(PackageName packageName, String name, String systemIncludeFile)
    {
        this(packageName, name, systemIncludeFile, false);
    }

    public NativeRuntimeType(PackageName packageName, String name, String systemIncludeFile, boolean isSimple)
    {
        super(packageName, name, isSimple);

        addSystemIncludeFile(systemIncludeFile);
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
