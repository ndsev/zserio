package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeStringType extends CppNativeType
{
    public NativeStringType()
    {
        super(STD_PACKAGE_NAME, "string");
        addSystemIncludeFile("string");
    }

    private static final PackageName STD_PACKAGE_NAME = new PackageName.Builder().addId("std").get();
}
