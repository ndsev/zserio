package zserio.emit.java.types;

import zserio.ast.PackageName;

public class NativeBitBufferType extends JavaNativeType
{
    public NativeBitBufferType()
    {
        super(RUNTIME_IO_PACKAGE, "BitBuffer");
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private static final PackageName RUNTIME_IO_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("io").get();
}
