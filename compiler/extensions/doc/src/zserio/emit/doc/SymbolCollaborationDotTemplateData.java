package zserio.emit.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.AstNode;
import zserio.ast.PackageName;

public class SymbolCollaborationDotTemplateData
{
    public SymbolCollaborationDotTemplateData(TemplateDataContext context, AstNode symbol,
            Iterable<AstNode> usedSymbols, Iterable<AstNode> usedBySymbols)
    {
        symbolName = AstNodeNameMapper.getName(symbol);
        relations = new ArrayList<Relation>();

        final Map<PackageName, Package> nameToPackageMap = new HashMap<PackageName, Package>();

        addSymbol(context, symbol, nameToPackageMap);

        for (AstNode usedSymbol : usedSymbols)
        {
            addSymbol(context, usedSymbol, nameToPackageMap);
            relations.add(new Relation(symbolName, AstNodeNameMapper.getName(usedSymbol)));
        }

        for (AstNode usedBySymbol : usedBySymbols)
        {
            addSymbol(context, usedBySymbol, nameToPackageMap);
            relations.add(new Relation(AstNodeNameMapper.getName(usedBySymbol), symbolName));
        }

        packages = nameToPackageMap.values();
    }

    public String getSymbolName()
    {
        return symbolName;
    }

    public Iterable<Package> getPackages()
    {
        return packages;
    }

    public Iterable<Relation> getRelations()
    {
        return relations;
    }

    public static class Package
    {
        public Package(String name)
        {
            this.name = name;
            symbols = new ArrayList<SymbolTemplateData>();
        }

        public void addSymbol(SymbolTemplateData symbol)
        {
            symbols.add(symbol);
        }

        public String getName()
        {
            return name;
        }

        public Iterable<SymbolTemplateData> getSymbols()
        {
            return symbols;
        }

        private final String name;
        private final List<SymbolTemplateData> symbols;
    }

    public static class Relation
    {
        public Relation(String symbolNameFrom, String symbolNameTo)
        {
            this.symbolNameFrom = symbolNameFrom;
            this.symbolNameTo = symbolNameTo;
        }

        public String getSymbolNameFrom()
        {
            return symbolNameFrom;
        }

        public String getSymbolNameTo()
        {
            return symbolNameTo;
        }

        private final String symbolNameFrom;
        private final String symbolNameTo;
    }

    private void addSymbol(TemplateDataContext context, AstNode symbol,
            Map<PackageName, Package> nameToPackageMap)
    {
        final PackageName packageName =
                AstNodePackageNameMapper.getPackageName(symbol, context.getPackageMapper());
        final String packageNameString = (packageName == null) ? "" : packageName.toString();
        Package packageInst = nameToPackageMap.get(packageName);
        if (packageInst == null)
        {
            packageInst = new Package(packageNameString);
            nameToPackageMap.put(packageName, packageInst);
        }

        packageInst.addSymbol(SymbolTemplateDataCreator.createData(context, symbol));
    }

    private final String symbolName;
    private final Iterable<Package> packages;
    private final List<Relation> relations;
}
