package zserio.emit.cpp;

import java.util.Collection;
import java.util.TreeSet;

import zserio.emit.common.PackageMapper;
import zserio.emit.cpp.types.CppNativeType;
import zserio.tools.HashUtil;

public abstract class CppTemplateData implements IncludeCollector
{
    public CppTemplateData(TemplateDataContext context)
    {
        generatorDescription = "Zserio C++ extension version " + CppExtensionVersion.VERSION_STRING;

        final PackageMapper cppPackageMapper = context.getCppPackageMapper();
        rootPackage =
                new Package(cppPackageMapper.getRootPackageName(), cppPackageMapper.getRootPackagePath());
        withWriterCode = context.getWithWriterCode();
        withInspectorCode = context.getWithInspectorCode();
        withValidationCode = context.getWithValidationCode();

        headerSystemIncludes = new TreeSet<String>();
        headerUserIncludes = new TreeSet<String>();
        cppUserIncludes = new TreeSet<String>();
        cppSystemIncludes = new TreeSet<String>();
        forwardDeclarations = new TreeSet<ForwardDeclarationTemplateData>();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public Package getRootPackage()
    {
        return rootPackage;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithInspectorCode()
    {
        return withInspectorCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
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
     * A sequence of types to be forward-declared in the generated H.
     */
    public Iterable<ForwardDeclarationTemplateData> getForwardDeclarations()
    {
        return forwardDeclarations;
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
        headerSystemIncludes.addAll(nativeType.getSystemIncludeFiles());
        headerUserIncludes.addAll(nativeType.getUserIncludeFiles());
    }

    @Override
    public void addHeaderSystemIncludes(Collection<String> systemIncludes)
    {
        headerSystemIncludes.addAll(systemIncludes);
    }

    @Override
    public void addHeaderForwardDeclarationsForType(CppNativeType nativeType)
    {
        forwardDeclarations.add(new ForwardDeclarationTemplateData(nativeType));
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

    public static class Package implements Comparable<Package>
    {
        public Package(CppNativeType type)
        {
            this(type.getNamespace(), type.getNamespacePath());
        }

        public Package(String name, Iterable<String> path)
        {
            this.name = name;
            this.path = path;
        }

        public String getName()
        {
            return name;
        }

        public Iterable<String> getPath()
        {
            return path;
        }

        @Override
        public int compareTo(Package other)
        {
            return name.compareTo(other.name);
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof Package)
            {
                return compareTo((Package)other) == 0;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, name);
            return hash;
        }

        private final String name;
        private final Iterable<String> path;
    }

    private final String generatorDescription;
    private final Package rootPackage;

    private final boolean withWriterCode;
    private final boolean withInspectorCode;
    private final boolean withValidationCode;

    private final TreeSet<String> headerSystemIncludes;
    private final TreeSet<String> headerUserIncludes;
    private final TreeSet<String> cppUserIncludes;
    private final TreeSet<String> cppSystemIncludes;
    private final TreeSet<ForwardDeclarationTemplateData> forwardDeclarations;
}
