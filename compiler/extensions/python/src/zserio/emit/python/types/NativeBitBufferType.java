package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeBitBufferType extends PythonNativeType
{
    public NativeBitBufferType()
    {
        super(BIT_BUFFER_PACKAGE_NAME, "BitBuffer");
    }

    private static final PackageName BIT_BUFFER_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
