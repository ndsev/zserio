package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(PythonNativeType elementType)
    {
        super(ARRAY_PACKAGE_NAME, "Array");
        this.elementType = elementType;
    }

    public PythonNativeType getElementType()
    {
        return elementType;
    }

    private final PythonNativeType elementType;
    private static final PackageName ARRAY_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
}
