package zserio.extension.doc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.AstNode;
import zserio.ast.Package;

/**
 * FreeMarker template data for Symbol collaboration dot emitter.
 */
public class SymbolCollaborationDotTemplateData
{
    public SymbolCollaborationDotTemplateData(TemplateDataContext context, AstNode symbol,
            Iterable<AstNode> usedSymbols, Iterable<AstNode> usedBySymbols)
    {
        symbolTemplateData =
                SymbolTemplateDataCreator.createTemplateInstantiationReferenceData(context, symbol);
        relations = new ArrayList<RelationTemplateData>();

        // use LinkedHashMap to generate always the same ordering
        final Map<Package, PackageTemplateData> packageToTemplateDataMap =
                new LinkedHashMap<Package, PackageTemplateData>();

        addSymbol(context, symbol, symbolTemplateData, packageToTemplateDataMap);

        for (AstNode usedSymbol : usedSymbols)
        {
            final SymbolTemplateData usedSymbolTemplateData =
                    SymbolTemplateDataCreator.createTemplateInstantiationReferenceData(context, usedSymbol);
            addSymbol(context, usedSymbol, usedSymbolTemplateData, packageToTemplateDataMap);
            relations.add(new RelationTemplateData(symbolTemplateData, usedSymbolTemplateData));
        }

        for (AstNode usedBySymbol : usedBySymbols)
        {
            final SymbolTemplateData usedBySymbolTemplateData =
                    SymbolTemplateDataCreator.createTemplateInstantiationReferenceData(context, usedBySymbol);
            addSymbol(context, usedBySymbol, usedBySymbolTemplateData, packageToTemplateDataMap);
            relations.add(new RelationTemplateData(usedBySymbolTemplateData, symbolTemplateData));
        }

        packages = packageToTemplateDataMap.values();
    }

    public SymbolTemplateData getSymbol()
    {
        return symbolTemplateData;
    }

    public Iterable<PackageTemplateData> getPackages()
    {
        return packages;
    }

    public Iterable<RelationTemplateData> getRelations()
    {
        return relations;
    }

    public static class PackageTemplateData
    {
        public PackageTemplateData(TemplateDataContext context, Package pkg)
        {
            symbol = SymbolTemplateDataCreator.createData(context, pkg);
            symbols = new ArrayList<SymbolTemplateData>();
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public Iterable<SymbolTemplateData> getSymbols()
        {
            return symbols;
        }

        void addSymbol(SymbolTemplateData symbol)
        {
            symbols.add(symbol);
        }

        private final SymbolTemplateData symbol;
        private final List<SymbolTemplateData> symbols;
    }

    public static class RelationTemplateData
    {
        public RelationTemplateData(SymbolTemplateData symbolFrom, SymbolTemplateData symbolTo)
        {
            this.symbolFrom = symbolFrom;
            this.symbolTo = symbolTo;
        }

        public SymbolTemplateData getSymbolFrom()
        {
            return symbolFrom;
        }

        public SymbolTemplateData getSymbolTo()
        {
            return symbolTo;
        }

        private final SymbolTemplateData symbolFrom;
        private final SymbolTemplateData symbolTo;
    }

    private void addSymbol(TemplateDataContext context, AstNode symbol, SymbolTemplateData symbolTemplateData,
            Map<Package, PackageTemplateData> packageToTemplateDataMap)
    {
        final Package packageNode = AstNodePackageMapper.getPackage(symbol);
        if (packageNode != null) // shall not be null!
        {
            PackageTemplateData packageTemplateData = packageToTemplateDataMap.get(packageNode);
            if (packageTemplateData == null)
            {
                packageTemplateData = new PackageTemplateData(context, packageNode);
                packageToTemplateDataMap.put(packageNode, packageTemplateData);
            }

            packageTemplateData.addSymbol(symbolTemplateData);
        }
    }

    private final SymbolTemplateData symbolTemplateData;
    private final Iterable<PackageTemplateData> packages;
    private final List<RelationTemplateData> relations;
}
