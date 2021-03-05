package zserio.extension.python;

import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.extension.python.types.PythonNativeType;

public interface ImportCollector
{
    void importPackage(String packageName);
    void importSymbol(PythonNativeSymbol nativeSymbol);
    void importType(PythonNativeType nativeType);
}
