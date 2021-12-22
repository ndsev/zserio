package zserio.extension.cpp.types;

import java.util.SortedSet;

import zserio.ast.PackageName;

/**
 * C++ native type interface.
 */
public interface CppNativeType
{
    /**
     * Returns the full name of the type.
     *
     * @return The name of the type including package or namespace name (if the type is contained in one).
     */
    public String getFullName();

    /**
     * Returns the short name of the type.
     *
     * @return The name of the type excluding package or namespace name.
     */
    public String getName();

    /**
     * Return name of the package which contains this type.
     *
     * @return The name of the package which contains this type.
     */
    public PackageName getPackageName();

    /**
     * Returns true iff the type is a "simple" type.
     *
     * In this context in means the type is small enough is that is should be passed by value.
     * It must also be constructible by the syntax "Type()".
     *
     * The various integral and float types return true here, all other types return false.
     *
     * @return true if the type is a simple type, otherwise false.
     */
    public boolean isSimpleType();

    /**
     * Returns a list with names of files to be included by '#include &lt;...&gt;'.
     *
     * @return The list of system include files.
     */
    public SortedSet<String> getSystemIncludeFiles();

    /**
     * Returns a list with names of files to be included by '#include "..."'.
     *
     * @return The list of user include files.
     */
    public SortedSet<String> getUserIncludeFiles();
}
