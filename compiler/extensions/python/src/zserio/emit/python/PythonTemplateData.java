package zserio.emit.python;

import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.emit.python.types.NativeBuiltinType;
import zserio.emit.python.types.PythonNativeType;
import zserio.tools.HashUtil;

public class PythonTemplateData implements ImportCollector
{
    public PythonTemplateData(TemplateDataContext context)
    {
        withWriterCode = context.getWithWriterCode();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public Iterable<String> getPackageImports()
    {
        return packageImports;
    }

    public Iterable<TypeImportTemplateData> getTypeImports()
    {
        return typeImports;
    }

    @Override
    public void importRuntimePackage()
    {
        packageImports.add("zserio");
    }

    @Override
    public void importType(PythonNativeType nativeType)
    {
        if (nativeType instanceof NativeBuiltinType)
            return;

        typeImports.add(new TypeImportTemplateData(nativeType));
    }

    public static class TypeImportTemplateData implements Comparable<TypeImportTemplateData>
    {
        public TypeImportTemplateData(PythonNativeType nativeType)
        {
            this.packageName = nativeType.getPackageName();
            this.moduleName = nativeType.getName();
            this.packagePath = PythonFullNameFormatter.getFullName(packageName);
        }

        public String getPackagePath()
        {
            return packagePath;
        }

        public String getModuleName()
        {
            return moduleName;
        }


        @Override
        public boolean equals(Object otherObject)
        {
            if (!(otherObject instanceof TypeImportTemplateData))
                    return false;

            // packagePath and moduleName uniquely define the type import
            TypeImportTemplateData other = (TypeImportTemplateData)otherObject;
            return packageName.equals(other.packageName) && moduleName.equals(other.moduleName);
        }

        @Override
        public int hashCode()
        {
            // packagePath and moduleName uniquely define the type import
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, packageName);
            hash = HashUtil.hash(hash, moduleName);

            return hash;
        }

        @Override
        public int compareTo(TypeImportTemplateData other)
        {
            // packagePath and moduleName uniquely define the type import
            int result = packageName.compareTo(other.packageName);
            if (result != 0)
                return result;
            return moduleName.compareTo(other.moduleName);
        }

        private final PackageName packageName;
        private final String moduleName;
        private final String packagePath;
    }

    private static final String generatorDescription =
            "Zserio Python extension version " + PythonExtensionVersion.VERSION_STRING;

    private final boolean withWriterCode;

    private final TreeSet<String> packageImports = new TreeSet<String>();
    private final TreeSet<TypeImportTemplateData> typeImports = new TreeSet<TypeImportTemplateData>();
}
