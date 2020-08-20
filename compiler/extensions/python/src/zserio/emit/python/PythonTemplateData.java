package zserio.emit.python;

import java.util.LinkedHashSet;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.emit.python.symbols.PythonNativeSymbol;
import zserio.emit.python.types.PythonNativeType;
import zserio.emit.python.types.NativeUserType;
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

    public Iterable<ImportTemplateData> getTypeImports()
    {
        return typeImports;
    }

    public Iterable<ImportTemplateData> getSymbolImports()
    {
        return symbolImports;
    }

    @Override
    public void importPackage(String packageName)
    {
        packageImports.add(packageName);
    }

    @Override
    public void importSymbol(PythonNativeSymbol nativeSymbol)
    {
        symbolImports.add(new ImportTemplateData(nativeSymbol));
    }

    @Override
    public void importType(PythonNativeType nativeType)
    {
        if (nativeType instanceof NativeUserType)
            typeImports.add(new ImportTemplateData(nativeType));
    }

    @Override
    public void importUsedType(PythonNativeType nativeType)
    {
        if (nativeType instanceof NativeUserType)
            typeImports.add(new ImportTemplateData(nativeType));
    }

    public static class ImportTemplateData implements Comparable<ImportTemplateData>
    {
        public ImportTemplateData(PythonNativeType nativeType)
        {
            this.packageName = nativeType.getPackageName();
            this.moduleName = nativeType.getName();
            this.packagePath = PythonFullNameFormatter.getFullName(packageName);
        }

        public ImportTemplateData(PythonNativeSymbol nativeSymbol)
        {
            this.packageName = nativeSymbol.getPackageName();
            this.moduleName = nativeSymbol.getName();
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
            if (!(otherObject instanceof ImportTemplateData))
                    return false;

            // packagePath and moduleName uniquely define the type import
            ImportTemplateData other = (ImportTemplateData)otherObject;
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
        public int compareTo(ImportTemplateData other)
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

    // keep ordering to have system imports before application imports
    private final LinkedHashSet<String> packageImports = new LinkedHashSet<String>();

    private final TreeSet<ImportTemplateData> typeImports = new TreeSet<ImportTemplateData>();
    private final TreeSet<ImportTemplateData> symbolImports = new TreeSet<ImportTemplateData>();
}
