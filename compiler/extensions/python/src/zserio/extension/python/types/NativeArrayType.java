package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python array type mapping.
 */
public final class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(NativeArrayTraits arrayTraits)
    {
        super(ZSERIO_PACKAGE_NAME, "array", "Array", arrayTraits);
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
