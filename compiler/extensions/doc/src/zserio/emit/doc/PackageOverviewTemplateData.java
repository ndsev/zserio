package zserio.emit.doc;

import java.util.Set;
import java.util.TreeSet;

import zserio.ast.Package;

public class PackageOverviewTemplateData
{
    public PackageOverviewTemplateData(TemplateDataContext context, Iterable<Package> packages)
    {
        packageSymbols = new TreeSet<PackageSymbol>();
        for (Package pkg : packages)
        {
            final String packageName = AstNodePackageNameMapper.getPackageName(pkg).toString();
            final SymbolTemplateData symbol = SymbolTemplateDataCreator.createData(context, pkg);
            packageSymbols.add(new PackageSymbol(packageName, symbol));
        }
    }

    public Iterable<PackageSymbol> getPackageSymbols()
    {
        return packageSymbols;
    }

    public static class PackageSymbol implements Comparable<PackageSymbol>
    {
        public PackageSymbol(String packageName, SymbolTemplateData symbol)
        {
            this.packageName = packageName;
            this.symbol = symbol;
        }

        @Override
        public int compareTo(PackageSymbol other)
        {
            int result = symbol.getName().compareTo(other.symbol.getName());
            if (result == 0)
                result = packageName.compareTo(other.packageName);

            return result;
        }

        @Override
        public boolean equals(Object other)
        {
            if ( !(other instanceof PackageSymbol) )
                return false;

            return (this == other) || compareTo((PackageSymbol)other) == 0;
        }

        @Override
        public int hashCode()
        {
            final String hashString = symbol.getName() + packageName;

            return hashString.hashCode();
        }

        public String getPackageName()
        {
            return packageName;
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        private final String packageName;
        private final SymbolTemplateData symbol;
    }

    private final Set<PackageSymbol> packageSymbols;
}
