package zserio.extension.cpp.types;

import java.util.SortedSet;

import zserio.ast.PackageName;

/**
 * C++ native type interface.
 */
public interface CppNativeType
{
    public String getFullName();

    public String getName();

    public PackageName getPackageName();

    public boolean isSimpleType();

    public SortedSet<String> getSystemIncludeFiles();

    public SortedSet<String> getUserIncludeFiles();
}
