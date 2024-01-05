package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.CompatibilityVersion;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for beginning of the package used by Package emitter.
 */
public final class BeginPackageTemplateData
{
    public BeginPackageTemplateData(PackageTemplateDataContext context, Package pkg,
            Map<Package, List<AstNode>> nodesMap, HeaderNavigationTemplateData headerNavigation)
            throws ZserioExtensionException
    {
        cssDirectory = context.getCssDirectory();
        isDefaultPackage = pkg.getPackageName().isEmpty();
        symbol = SymbolTemplateDataCreator.createData(context, pkg);
        docComments = new DocCommentsTemplateData(context, pkg.getDocComments());

        this.headerNavigation = headerNavigation;

        compatibilityVersion = pkg.getCompatibilityVersion() != null
                ? new CompatibilityVersionTemplateData(context, pkg.getCompatibilityVersion())
                : null;

        for (Import importNode : pkg.getImports())
            importNodes.add(new ImportTemplateData(context, importNode));

        for (Map.Entry<Package, List<AstNode>> entry : nodesMap.entrySet())
            packages.add(new PackageSymbolOverviewTemplateData(context, entry.getKey(), entry.getValue()));

        for (AstNode packageSymbol : nodesMap.get(pkg))
            tocSymbols.add(SymbolTemplateDataCreator.createData(context, packageSymbol));
    }

    public String getCssDirectory()
    {
        return cssDirectory;
    }

    public String getStylesheetName()
    {
        return StylesheetEmitter.STYLESHEET_FILE_NAME;
    }

    public boolean getIsDefaultPackage()
    {
        return isDefaultPackage;
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public HeaderNavigationTemplateData getHeaderNavigation()
    {
        return headerNavigation;
    }

    public CompatibilityVersionTemplateData getCompatibilityVersion()
    {
        return compatibilityVersion;
    }

    public Iterable<ImportTemplateData> getImportNodes()
    {
        return importNodes;
    }

    public Iterable<PackageSymbolOverviewTemplateData> getPackages()
    {
        return packages;
    }

    public Iterable<SymbolTemplateData> getTocSymbols()
    {
        return tocSymbols;
    }

    public static final class PackageSymbolOverviewTemplateData
            implements Comparable<PackageSymbolOverviewTemplateData>
    {
        public PackageSymbolOverviewTemplateData(
                PackageTemplateDataContext context, Package pkg, List<AstNode> packageSymbols)
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
        // we want to have sorted symbols in symbol overview
        private final Set<SymbolTemplateData> packageSymbols = new TreeSet<SymbolTemplateData>();
    }

    public static final class CompatibilityVersionTemplateData
    {
        public CompatibilityVersionTemplateData(
                PackageTemplateDataContext context, CompatibilityVersion compatibilityVersion)
        {
            version = compatibilityVersion.getVersion().toString();
            docComments = new DocCommentsTemplateData(context, compatibilityVersion.getDocComments());
        }

        public String getVersion()
        {
            return version;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String version;
        private final DocCommentsTemplateData docComments;
    }

    public static final class ImportTemplateData
    {
        public ImportTemplateData(PackageTemplateDataContext context, Import importNode)
                throws ZserioExtensionException
        {
            docComments = new DocCommentsTemplateData(context, importNode.getDocComments());
            importedPackageSymbol =
                    SymbolTemplateDataCreator.createData(context, importNode.getImportedPackage());
            final PackageSymbol importedPackageSymbol = importNode.getImportedSymbol();
            importedSymbol = (importedPackageSymbol == null)
                    ? null
                    : SymbolTemplateDataCreator.createData(context, importedPackageSymbol);
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

    private final String cssDirectory;
    private final boolean isDefaultPackage;
    private final SymbolTemplateData symbol;
    private final DocCommentsTemplateData docComments;
    private final HeaderNavigationTemplateData headerNavigation;
    private final CompatibilityVersionTemplateData compatibilityVersion;
    private final List<ImportTemplateData> importNodes = new ArrayList<ImportTemplateData>();
    // we want to have sorted packages in symbol overview
    private final Set<PackageSymbolOverviewTemplateData> packages =
            new TreeSet<PackageSymbolOverviewTemplateData>();
    // we want to have symbols in the order of definition in ToC
    private final List<SymbolTemplateData> tocSymbols = new ArrayList<SymbolTemplateData>();
}
