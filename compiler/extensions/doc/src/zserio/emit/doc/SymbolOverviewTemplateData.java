package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.ast.TemplateParameter;
import zserio.ast.ZserioTemplatableType;

public class SymbolOverviewTemplateData
{
    public SymbolOverviewTemplateData(TemplateDataContext context,  Map<Package, List<AstNode>> nodesMap)
    {
        packageNames = new TreeSet<String>();
        packageSymbols = new TreeSet<PackageSymbol>();

        for (Map.Entry<Package, List<AstNode>> nodesEntry : nodesMap.entrySet())
        {
            final Package pkg = nodesEntry.getKey();
            final String packageName = pkg.getPackageName().toString();
            packageNames.add(packageName);
            for (AstNode node : nodesEntry.getValue())
            {
                final SymbolTemplateData symbol = SymbolTemplateDataCreator.createData(context, node);

                // TODO[Mi-L@]: Should the template parameters be a part of symbol? Or at lest included within
                //              the rendered <a> tag?
                final ArrayList<String> templateParameters = new ArrayList<String>();
                if (node instanceof ZserioTemplatableType)
                {
                    for (TemplateParameter templateParameter :
                            ((ZserioTemplatableType)node).getTemplateParameters())
                    {
                        templateParameters.add(templateParameter.getName());
                    }
                }
                packageSymbols.add(new PackageSymbol(packageName, symbol, templateParameters));
            }
        }
    }

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    public Set<PackageSymbol> getPackageSymbols()
    {
        return packageSymbols;
    }

    public static class PackageSymbol implements Comparable<PackageSymbol>
    {
        public PackageSymbol(String packageName, SymbolTemplateData symbol, List<String> templateParameters)
        {
            this.packageName = packageName;
            this.symbol = symbol;
            this.templateParameters = templateParameters;
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

        public List<String> getTemplateParameters()
        {
            return templateParameters;
        }

        private final String packageName;
        private final SymbolTemplateData symbol;
        private final List<String> templateParameters;
    }

    private final Set<String> packageNames;
    private final Set<PackageSymbol> packageSymbols;
}
