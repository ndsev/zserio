package zserio.emit.cpp98.types;

import zserio.ast.PackageName;

public class NativeStringType extends CppNativeType
{
    public NativeStringType()
    {
        super(STD_PACKAGE_NAME, "string", false);
        addSystemIncludeFile("string");
    }

    private static final PackageName STD_PACKAGE_NAME = new PackageName.Builder().addId("std").get();
}
