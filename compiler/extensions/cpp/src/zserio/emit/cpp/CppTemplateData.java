package zserio.emit.cpp;

import java.util.Collection;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.emit.common.PackageMapper;
import zserio.emit.cpp.types.CppNativeType;

public abstract class CppTemplateData implements IncludeCollector
{
    public CppTemplateData(TemplateDataContext context)
    {
        generatorDescription = "Zserio C++ extension version " + CppExtensionVersion.VERSION_STRING;

        withWriterCode = context.getWithWriterCode();

        headerSystemIncludes = new TreeSet<String>();
        headerUserIncludes = new TreeSet<String>();
        cppUserIncludes = new TreeSet<String>();
        cppSystemIncludes = new TreeSet<String>();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public Iterable<String> getHeaderSystemIncludes()
    {
        return headerSystemIncludes;
    }

    public Iterable<String> getHeaderUserIncludes()
    {
        return headerUserIncludes;
    }

    /**
     * A sequence of user file names to be #included from the generated CPP (as opposed to H).
     */
    public Iterable<String> getCppUserIncludes()
    {
        return cppUserIncludes;
    }

    /**
     * A sequence of system file names to be #included from the generated CPP (as opposed to H).
     */
    public Iterable<String> getCppSystemIncludes()
    {
        return cppSystemIncludes;
    }

    @Override
    public void addHeaderIncludesForType(CppNativeType nativeType)
    {
        addHeaderSystemIncludes(nativeType.getSystemIncludeFiles());
        addHeaderUserIncludes(nativeType.getUserIncludeFiles());
    }

    @Override
    public void addHeaderSystemIncludes(Collection<String> systemIncludes)
    {
        headerSystemIncludes.addAll(systemIncludes);
    }

    @Override
    public void addHeaderUserIncludes(Collection<String> userIncludes)
    {
        headerUserIncludes.addAll(userIncludes);
    }

    @Override
    public void addCppIncludesForType(CppNativeType nativeType)
    {
        cppSystemIncludes.addAll(nativeType.getSystemIncludeFiles());
        cppUserIncludes.addAll(nativeType.getUserIncludeFiles());
    }

    @Override
    public void addCppSystemIncludes(Collection<String> systemIncludes)
    {
        cppSystemIncludes.addAll(systemIncludes);
    }

    @Override
    public void addCppUserIncludes(Collection<String> userIncludes)
    {
        cppUserIncludes.addAll(userIncludes);
    }

    public static class PackageTemplateData
    {
        public PackageTemplateData(CppNativeType type)
        {
            this(type.getPackageName());
        }

        public PackageTemplateData(PackageName packageName)
        {
            this.packageName = packageName;
        }

        public String getName()
        {
            return CppFullNameFormatter.getFullName(packageName);
        }

        public Iterable<String> getPath()
        {
            return packageName.getIdList();
        }

        private final PackageName packageName;
    }

    private final String generatorDescription;

    private final boolean withWriterCode;

    private final TreeSet<String> headerSystemIncludes;
    private final TreeSet<String> headerUserIncludes;
    private final TreeSet<String> cppUserIncludes;
    private final TreeSet<String> cppSystemIncludes;
}
