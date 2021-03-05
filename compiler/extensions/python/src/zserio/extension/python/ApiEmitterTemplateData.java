package zserio.extension.python;

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import zserio.ast.PackageName;
import zserio.tools.HashUtil;

public class ApiEmitterTemplateData extends PythonTemplateData
{
    public ApiEmitterTemplateData(TemplateDataContext context, PackageName packageName)
    {
        super(context);
        this.packageName = packageName;
    }

    public Iterable<ApiImport> getSubpackages()
    {
        return subpackages;
    }

    public Iterable<ApiImport> getPackageSymbols()
    {
        return packageSymbols;
    }

    public static class ApiImport implements Comparable<ApiImport>
    {
        public ApiImport(String modulePath, String symbolName)
        {
            this.modulePath = modulePath;
            this.symbol = symbolName;
        }

        public String getModulePath()
        {
            return modulePath;
        }

        public String getSymbol()
        {
            return symbol;
        }

        @Override
        public boolean equals(Object otherObject)
        {
            if (!(otherObject instanceof ApiImport))
                    return false;

            final ApiImport other = (ApiImport)otherObject;

            return modulePath.equals(other.modulePath) && symbol.equals(other.symbol);
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, modulePath);
            hash = HashUtil.hash(hash, symbol);

            return hash;
        }

        @Override
        public int compareTo(ApiImport other)
        {
            int result = modulePath.compareTo(other.modulePath);
            if (result != 0)
                return result;
            return symbol.compareTo(other.symbol);
        }

        private final String modulePath;
        private final String symbol;
    }

    void addSubpackage(String subpackage)
    {
        // please note that adding of the same subpackage can be called several times
        final String modulePath = PythonFullNameFormatter.getFullModuleImportName(packageName, subpackage);
        subpackages.add(new ApiImport(modulePath, subpackage));
    }

    void addPackageSymbol(String symbolName)
    {
        final String modulePath = PythonFullNameFormatter.getFullModuleImportName(packageName, symbolName);
        packageSymbols.add(new ApiImport(modulePath, symbolName));
    }

    private final PackageName packageName;

    private final Set<ApiImport> subpackages = new TreeSet<ApiImport>();
    private final List<ApiImport> packageSymbols = new ArrayList<ApiImport>();
}
