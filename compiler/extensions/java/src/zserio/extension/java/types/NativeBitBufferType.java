package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeBitBufferType extends NativeArrayableType
{
    public NativeBitBufferType()
    {
        super(RUNTIME_IO_PACKAGE, "BitBuffer", new NativeArrayTraits("BitBufferArray") );
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private static final PackageName RUNTIME_IO_PACKAGE =
            new PackageName.Builder().addId("zserio").addId("runtime").addId("io").get();
}
