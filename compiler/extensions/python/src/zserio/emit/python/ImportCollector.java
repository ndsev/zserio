package zserio.emit.python;

import zserio.emit.python.types.PythonNativeType;

public interface ImportCollector
{
    void importPackage(String packageName);
    void importType(PythonNativeType nativeType);
    void importUsedType(PythonNativeType nativeType);
}
