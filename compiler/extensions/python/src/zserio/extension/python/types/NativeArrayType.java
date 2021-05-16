package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python array type mapping.
 */
public class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(NativeArrayTraits arrayTraits)
    {
        this("array", "Array", arrayTraits, null);
    }

    protected NativeArrayType(String moduleName, String name, NativeArrayTraits arrayTraits,
            String packedArrayTraitsName)
    {
        super(ZSERIO_PACKAGE_NAME, moduleName, name, arrayTraits);

        this.packedArrayTraitsName = packedArrayTraitsName;
    }

    public String getPackedArrayTraitsName()
    {
        return packedArrayTraitsName;
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();

    private final String packedArrayTraitsName;
}
