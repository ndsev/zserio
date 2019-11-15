package zserio.emit.python;

import zserio.emit.python.symbols.PythonNativeSymbol;
import zserio.emit.python.types.PythonNativeType;

public interface ImportCollector
{
    void importPackage(String packageName);
    void importSymbol(PythonNativeSymbol nativeSymbol);
    void importType(PythonNativeType nativeType);
    void importUsedType(PythonNativeType nativeType);
}
