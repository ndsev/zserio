package zserio.extension.cpp.types;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;

/**
 * C++ native type - e.g. compound type, subtype, etc.
 */
public class NativeType implements CppNativeType
{
    public NativeType(PackageName packageName, String name)
    {
        this(packageName, name, packageName.isEmpty());
    }

    public NativeType(PackageName packageName, String name, boolean simpleType)
    {
        this.packageName = packageName;
        this.name = name;
        this.systemIncludeFiles = new TreeSet<String>();
        this.userIncludeFiles = new TreeSet<String>();
        this.simpleType = simpleType;
    }

    @Override
    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(packageName, name);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public PackageName getPackageName()
    {
        return packageName;
    }

    @Override
    public boolean isSimpleType()
    {
        return simpleType;
    }

    @Override
    public SortedSet<String> getSystemIncludeFiles()
    {
        return Collections.unmodifiableSortedSet(systemIncludeFiles);
    }

    @Override
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
