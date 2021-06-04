package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python subtype mapping.
 */
public class NativeSubtype extends NativeUserType
{
    public NativeSubtype(PackageName packageName, String name, PythonNativeType nativeTargetBaseType)
    {
        super(packageName, name, nativeTargetBaseType.getArrayTraits());
        this.nativeTargetBaseType = nativeTargetBaseType;
    }

    public PythonNativeType getNativeTargetBaseType()
    {
        return nativeTargetBaseType;
    }

    private final PythonNativeType nativeTargetBaseType;
}
