package zserio.extension.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;

public class SymbolCollaborationDotTemplateData
{
    public SymbolCollaborationDotTemplateData(TemplateDataContext context, AstNode symbol,
            Iterable<AstNode> usedSymbols, Iterable<AstNode> usedBySymbols)
    {
        final AstNode symbolNode = getInstantiationReference(symbol);
        final SymbolTemplateData symbolTemplateData = SymbolTemplateDataCreator.createData(context, symbolNode);
        symbolName = getUniqueName(symbolTemplateData);
        relations = new ArrayList<RelationTemplateData>();

        // use LinkedHashMap to generate always the same ordering
        final Map<Package, PackageTemplateData> packageToTemplateDataMap =
                new LinkedHashMap<Package, PackageTemplateData>();

        addSymbol(context, symbol, symbolTemplateData, packageToTemplateDataMap);

        for (AstNode usedSymbol : usedSymbols)
        {
            final AstNode usedSymbolNode = getInstantiationReference(usedSymbol);
            final SymbolTemplateData usedSymbolTemplateData =
                    SymbolTemplateDataCreator.createData(context, usedSymbolNode);
            addSymbol(context, usedSymbol, usedSymbolTemplateData, packageToTemplateDataMap);
            relations.add(new RelationTemplateData(symbolName, getUniqueName(usedSymbolTemplateData)));
        }

        for (AstNode usedBySymbol : usedBySymbols)
        {
            final AstNode usedBySymbolNode = getInstantiationReference(usedBySymbol);
            final SymbolTemplateData usedBySymbolTemplateData =
                    SymbolTemplateDataCreator.createData(context, usedBySymbolNode);
            addSymbol(context, usedBySymbol, usedBySymbolTemplateData, packageToTemplateDataMap);
            relations.add(new RelationTemplateData(getUniqueName(usedBySymbolTemplateData), symbolName));
        }

        packages = packageToTemplateDataMap.values();
    }

    public String getSymbolName()
    {
        return symbolName;
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
        public PackageTemplateData(String name)
        {
            this.name = name;
            namedSymbols = new ArrayList<NamedSymbolTemplateData>();
        }

        public String getName()
        {
            return name;
        }

        public Iterable<NamedSymbolTemplateData> getNamedSymbols()
        {
            return namedSymbols;
        }

        public static class NamedSymbolTemplateData
        {
            public NamedSymbolTemplateData(String name, SymbolTemplateData symbol)
            {
                this.name = name;
                this.symbol = symbol;
            }

            public String getName()
            {
                return name;
            }

            public SymbolTemplateData getSymbol()
            {
                return symbol;
            }

            private final String name;
            private final SymbolTemplateData symbol;
        }

        void addSymbol(String name, SymbolTemplateData symbol)
        {
            namedSymbols.add(new NamedSymbolTemplateData(name, symbol));
        }

        private final String name;
        private final List<NamedSymbolTemplateData> namedSymbols;
    }

    public static class RelationTemplateData
    {
        public RelationTemplateData(String symbolNameFrom, String symbolNameTo)
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

    private void addSymbol(TemplateDataContext context, AstNode symbol, SymbolTemplateData symbolTemplateData,
            Map<Package, PackageTemplateData> packageToTemplateDataMap)
    {
        final Package pkg = AstNodePackageMapper.getPackage(symbol);
        PackageTemplateData packageInst = packageToTemplateDataMap.get(pkg);
        if (packageInst == null)
        {
            final String packageFileName = (pkg == null) ? "" : PackageFileNameMapper.getFileName(pkg);
            packageInst = new PackageTemplateData(packageFileName);
            packageToTemplateDataMap.put(pkg, packageInst);
        }

        packageInst.addSymbol(getUniqueName(symbolTemplateData), symbolTemplateData);
    }

    // TODO[Mi-L@]: Shoudn't we compose the unique name in FTL?
    //              But note that the relations map is based only on strings.
    private static String getUniqueName(SymbolTemplateData symbol)
    {
        StringBuffer arguments = new StringBuffer();
        for (SymbolTemplateData templateArgument : symbol.getTemplateArguments())
        {
            if (arguments.length() > 0)
                arguments.append(",");
            arguments.append(getUniqueName(templateArgument));
        }

        if (arguments.length() == 0)
            return symbol.getName();

        return symbol.getName() + "<" + arguments.toString() + ">";
    }

    // TODO[Mi-L@]: This same logic is used on several places. Improve! See e.g. HtmlTemplateData.
    private static AstNode getInstantiationReference(AstNode node)
    {
        // use instantiation reference instead of instantiation to get template with it's argument
        if (node instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType instance = (ZserioTemplatableType)node;
            final ZserioTemplatableType template = instance.getTemplate();
            if (template != null)
            {
                final Iterator<TypeReference> instantiationReferenceIterator =
                        instance.getInstantiationReferenceStack().iterator();
                if (instantiationReferenceIterator.hasNext())
                    return instantiationReferenceIterator.next();
            }
        }
        return node;
    }

    private final String symbolName;
    private final Iterable<PackageTemplateData> packages;
    private final List<RelationTemplateData> relations;
}
