package zserio.emit.python;

import zserio.emit.python.types.PythonNativeType;

public interface ImportCollector
{
    void importRuntime();
    void importType(PythonNativeType nativeType);
}
