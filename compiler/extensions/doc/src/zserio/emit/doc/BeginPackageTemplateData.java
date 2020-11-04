package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.emit.common.ZserioEmitException;

public class BeginPackageTemplateData
{
    public BeginPackageTemplateData(TemplateDataContext context, Package pkg,
            Map<Package, List<AstNode>> nodesMap) throws ZserioEmitException
    {
        packageAnchor = AstNodeTypeNameMapper.getTypeName(pkg);
        symbol = SymbolTemplateDataCreator.createData(context, pkg);
        docComments = new DocCommentsTemplateData(context, pkg.getDocComments());

        for (Import importNode : pkg.getImports())
            importNodes.add(new ImportTemplateData(context, importNode));

        for (Map.Entry<Package, List<AstNode>> entry : nodesMap.entrySet())
            packages.add(new PackageSymbolOverviewTemplateData(context, entry.getKey(), entry.getValue()));
    }

    public String getStylesheetName()
    {
        return StylesheetEmitter.STYLESHEET_FILE_NAME;
    }

    public String getPackageAnchor()
    {
        return packageAnchor;
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public Iterable<ImportTemplateData> getImportNodes()
    {
        return importNodes;
    }

    public Iterable<PackageSymbolOverviewTemplateData> getPackages()
    {
        return packages;
    }

    public static class PackageSymbolOverviewTemplateData implements
            Comparable<PackageSymbolOverviewTemplateData>
    {
        PackageSymbolOverviewTemplateData(TemplateDataContext context, Package pkg,
                List<AstNode> packageSymbols)
        {
            symbol = SymbolTemplateDataCreator.createData(context, pkg);
            for (AstNode packageSymbol : packageSymbols)
                this.packageSymbols.add(SymbolTemplateDataCreator.createData(context, packageSymbol));
        }

        @Override
        public int compareTo(PackageSymbolOverviewTemplateData other)
        {
            return symbol.compareTo(other.symbol);
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof PackageSymbolOverviewTemplateData))
                return false;

            return (this == other) || compareTo((PackageSymbolOverviewTemplateData)other) == 0;
        }

        @Override
        public int hashCode()
        {
            return symbol.hashCode();
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public Iterable<SymbolTemplateData> getPackageSymbols()
        {
            return packageSymbols;
        }

        private final SymbolTemplateData symbol;
        // we want to have sorted symbols in the symbol overview
        private final Set<SymbolTemplateData> packageSymbols = new TreeSet<SymbolTemplateData>();
    }

    public static class ImportTemplateData
    {
        public ImportTemplateData(TemplateDataContext context, Import importNode) throws ZserioEmitException
        {
            docComments = new DocCommentsTemplateData(context, importNode.getDocComments());
            importedPackageSymbol = SymbolTemplateDataCreator.createData(context, importNode.getImportedPackage());
            final PackageSymbol importedPackageSymbol = importNode.getImportedSymbol();
            importedSymbol = (importedPackageSymbol == null) ? null :
                SymbolTemplateDataCreator.createData(context, importedPackageSymbol);
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public SymbolTemplateData getImportedPackageSymbol()
        {
            return importedPackageSymbol;
        }

        public SymbolTemplateData getImportedSymbol()
        {
            return importedSymbol;
        }

        private final DocCommentsTemplateData docComments;
        private final SymbolTemplateData importedPackageSymbol;
        private final SymbolTemplateData importedSymbol;
    };

    private final String packageAnchor;
    private final SymbolTemplateData symbol;
    private final DocCommentsTemplateData docComments;
    private final List<ImportTemplateData> importNodes = new ArrayList<ImportTemplateData>();
    private final Set<PackageSymbolOverviewTemplateData> packages =
            new TreeSet<PackageSymbolOverviewTemplateData>();
}
