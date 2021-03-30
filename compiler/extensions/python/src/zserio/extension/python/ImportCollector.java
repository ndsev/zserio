package zserio.extension.python;

import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.extension.python.types.PythonNativeType;

/**
 * Interface for import collectors.
 */
interface ImportCollector
{
    void importPackage(String packageName);
    void importSymbol(PythonNativeSymbol nativeSymbol);
    void importType(PythonNativeType nativeType);
}
