package zserio.emit.cpp.types;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.emit.common.NativeType;
import zserio.emit.cpp.CppFullNameFormatter;
import zserio.emit.cpp.CppUtil;

public abstract class CppNativeType implements NativeType
{
    public CppNativeType(List<String> namespacePath, String name, boolean simpleType)
    {
        this.namespacePath = namespacePath;
        this.namespace = CppUtil.makeNamespaceFromPath(namespacePath);
        this.name = name;
        this.systemIncludeFiles = new TreeSet<String>();
        this.userIncludeFiles = new TreeSet<String>();
        this.simpleType = simpleType;
    }

    @Override
    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(namespace, name);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Return the namespace that contains this type.
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Return the namespace that contains this type as a list of components.
     */
    public List<String> getNamespacePath()
    {
        return namespacePath;
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
            return CppUtil.formatConstRef(getFullName());
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
        systemIncludeFiles.add(include);
    }

    protected void addUserIncludeFile(String include)
    {
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

    private final List<String> namespacePath;
    private final String namespace;
    private final String name;
    private final SortedSet<String> systemIncludeFiles;
    private final SortedSet<String> userIncludeFiles;
    private final boolean simpleType;
}
