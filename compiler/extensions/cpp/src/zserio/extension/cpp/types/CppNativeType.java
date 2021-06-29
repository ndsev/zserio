package zserio.extension.cpp.types;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;

/**
 * C++ native type - e.g. compound type, subtype, etc.
 */
public abstract class CppNativeType
{
    public CppNativeType(PackageName packageName, String name)
    {
        this(packageName, name, packageName.isEmpty());
    }

    public CppNativeType(PackageName packageName, String name, boolean simpleType)
    {
        this.packageName = packageName;
        this.name = name;
        this.systemIncludeFiles = new TreeSet<String>();
        this.userIncludeFiles = new TreeSet<String>();
        this.simpleType = simpleType;
    }

    /**
     * Returns the full name of the type.
     *
     * @return The name of the type including package or namespace name (if the type is contained in one).
     */
    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(packageName, name);
    }

    /**
     * Returns the short name of the type.
     *
     * @return The name of the type excluding package or namespace name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return name of the package which contains this type.
     */
    public PackageName getPackageName()
    {
        return packageName;
    }

    /**
     * Returns a string representing the C++ type that should be used when passing this type as a function
     * argument.
     */
    public String getArgumentTypeName()
    {
        if (isSimpleType())
            return getFullName();
        else
            return "const " + getFullName() + '&';
    }

    /**
     * Returns true iff the type is a "simple" type.
     *
     * In this context in means the type is small enough is that is should be passed by value.
     * It must also be constructible by the syntax "Type()".
     *
     * The various integral and float types return true here, all other types return false.
     */
    public boolean isSimpleType()
    {
        return simpleType;
    }

    /**
     * Returns a list with names of files to be included by '#include <...>'.
     */
    public SortedSet<String> getSystemIncludeFiles()
    {
        return Collections.unmodifiableSortedSet(systemIncludeFiles);
    }

    /**
     * Returns a list with names of files to be included by '#include "..."'.
     */
    public SortedSet<String> getUserIncludeFiles()
    {
        return Collections.unmodifiableSortedSet(userIncludeFiles);
    }

    protected void addSystemIncludeFile(String include)
    {
        if (include != null)
            systemIncludeFiles.add(include);
    }

    protected void addUserIncludeFile(String include)
    {
        if (include != null)
            userIncludeFiles.add(include);
    }

    /**
     * Add system and user includes from another CppNativeType.
     */
    protected void addIncludeFiles(CppNativeType other)
    {
        for (String systemInclude : other.getSystemIncludeFiles())
        {
            addSystemIncludeFile(systemInclude);
        }

        for (String userInclude : other.getUserIncludeFiles())
        {
            addUserIncludeFile(userInclude);
        }
    }

    private final PackageName packageName;
    private final String name;
    private final SortedSet<String> systemIncludeFiles;
    private final SortedSet<String> userIncludeFiles;
    private final boolean simpleType;
}
