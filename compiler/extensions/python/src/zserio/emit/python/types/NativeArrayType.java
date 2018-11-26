package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(PythonNativeType elementType, String traitsName)
    {
        super(ARRAY_PACKAGE_NAME, "Array");

        this.elementType = elementType;
        this.traitsName = traitsName;
    }

    public String getTraitsName()
    {
        return traitsName;
    }

    // TODO TBR?!?
    public PythonNativeType getElementType()
    {
        return elementType;
    }

    private static final PackageName ARRAY_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();

    private final PythonNativeType elementType;
    private final String traitsName;
}
