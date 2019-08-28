package zserio.emit.cpp;

import java.util.Collection;

import zserio.emit.cpp.types.CppNativeType;

/**
 * Adapter which adds all includes as header includes.
 */
public class HeaderIncludeCollectorAdapter implements IncludeCollector
{
    public HeaderIncludeCollectorAdapter(IncludeCollector includeCollector)
    {
        this.includeCollector = includeCollector;
    }

    @Override
    public void addHeaderIncludesForType(CppNativeType nativeType)
    {
        includeCollector.addHeaderIncludesForType(nativeType);
    }

    @Override
    public void addHeaderSystemIncludes(Collection<String> systemIncludes)
    {
        includeCollector.addHeaderSystemIncludes(systemIncludes);
    }

    @Override
    public void addHeaderUserIncludes(Collection<String> userIncludes)
    {
        includeCollector.addHeaderUserIncludes(userIncludes);
    }

    @Override
    public void addCppIncludesForType(CppNativeType nativeType)
    {
        includeCollector.addHeaderIncludesForType(nativeType);
    }

    @Override
    public void addCppSystemIncludes(Collection<String> systemIncludes)
    {
        includeCollector.addHeaderSystemIncludes(systemIncludes);
    }

    @Override
    public void addCppUserIncludes(Collection<String> userIncludes)
    {
        includeCollector.addHeaderUserIncludes(userIncludes);
    }

    final IncludeCollector includeCollector;
}
