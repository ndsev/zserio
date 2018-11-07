package zserio.emit.python.types;

import java.util.Arrays;

public class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(PythonNativeType elementType)
    {
        super(Arrays.asList("zserio"), "Array");
        this.elementType = elementType;
    }

    public PythonNativeType getElementType()
    {
        return elementType;
    }

    private final PythonNativeType elementType;
}
