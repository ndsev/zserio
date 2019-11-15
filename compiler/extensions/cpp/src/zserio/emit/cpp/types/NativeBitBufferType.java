package zserio.emit.cpp.types;

import zserio.ast.PackageName;

public class NativeBitBufferType extends CppNativeType
{
    public NativeBitBufferType()
    {
        super(ZSERIO_PACKAGE_NAME, "BitBuffer");
        addSystemIncludeFile(BIT_BUFFER_INCLUDE);
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final String BIT_BUFFER_INCLUDE = "zserio/BitBuffer.h";
}
