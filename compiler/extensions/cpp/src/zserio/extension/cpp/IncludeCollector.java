package zserio.extension.cpp;

import java.util.Collection;

import zserio.extension.cpp.types.CppNativeType;

public interface IncludeCollector
{
    void addHeaderIncludesForType(CppNativeType nativeType);
    void addHeaderSystemIncludes(Collection<String> systemIncludes);
    void addHeaderUserIncludes(Collection<String> userIncludes);

    void addCppIncludesForType(CppNativeType nativeType);
    void addCppSystemIncludes(Collection<String> systemIncludes);
    void addCppUserIncludes(Collection<String> userIncludes);
}
