package zserio.emit.python;

import zserio.emit.python.types.PythonNativeType;

public interface ImportCollector
{
    void importRuntimePackage();
    void importType(PythonNativeType nativeType);
}
