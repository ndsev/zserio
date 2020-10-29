package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class BeginPackageTemplateData
{
    public BeginPackageTemplateData(TemplateDataContext context, Package pkg,
            Map<Package, List<AstNode>> nodesMap) throws ZserioEmitException
    {
        packageAnchor = AstNodeTypeNameMapper.getTypeName(pkg);
        symbol = SymbolTemplateDataCreator.createData(context, pkg);
        docComments = new DocCommentsTemplateData(context, pkg.getDocComments());

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

    public Iterable<PackageSymbolOverviewTemplateData> getPackages()
    {
        return packages;
    }

    public static class PackageSymbolOverviewTemplateData
    {
        PackageSymbolOverviewTemplateData(TemplateDataContext context, Package pkg,
                List<AstNode> packageSymbols)
        {
            symbol = SymbolTemplateDataCreator.createData(context, pkg);
            for (AstNode packageSymbol : packageSymbols)
                this.packageSymbols.add(new PackageSymbolTemplateData(context, packageSymbol));
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public Iterable<PackageSymbolTemplateData> getPackageSymbols()
        {
            return packageSymbols;
        }

        private final SymbolTemplateData symbol;
        // we want to have sorted symbols in the symbol overview
        private final Set<PackageSymbolTemplateData> packageSymbols = new TreeSet<PackageSymbolTemplateData>();
    }

    private final String packageAnchor;
    private final SymbolTemplateData symbol;
    private final DocCommentsTemplateData docComments;
    private final List<PackageSymbolOverviewTemplateData> packages =
            new ArrayList<PackageSymbolOverviewTemplateData>();
}
