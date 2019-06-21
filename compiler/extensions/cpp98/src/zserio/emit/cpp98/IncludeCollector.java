package zserio.emit.cpp98;

import java.util.Collection;

import zserio.emit.cpp98.types.CppNativeType;

public interface IncludeCollector
{
    void addHeaderIncludesForType(CppNativeType nativeType);
    void addHeaderSystemIncludes(Collection<String> systemIncludes);
    void addHeaderUserIncludes(Collection<String> userIncludes);
    void addHeaderForwardDeclarationsForType(CppNativeType nativeType);

    void addCppIncludesForType(CppNativeType nativeType);
    void addCppSystemIncludes(Collection<String> systemIncludes);
    void addCppUserIncludes(Collection<String> userIncludes);
}
