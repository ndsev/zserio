package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python BitBuffer mapping.
 */
public class NativeBitBufferType extends PythonNativeType
{
    public NativeBitBufferType()
    {
        super(BIT_BUFFER_PACKAGE_NAME, "bitbuffer", "BitBuffer", new NativeArrayTraits("BitBufferArrayTraits"));
    }

    private static final PackageName BIT_BUFFER_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
