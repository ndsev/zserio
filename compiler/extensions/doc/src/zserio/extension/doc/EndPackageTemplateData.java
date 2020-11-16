package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.extension.common.ZserioExtensionException;

public class EndPackageTemplateData
{
    public EndPackageTemplateData(PackageTemplateDataContext context, Package pkg,
            List<AstNode> packageSymbols) throws ZserioExtensionException
    {
        jsDirectory = context.getJsDirectory();
        symbol = SymbolTemplateDataCreator.createData(context, pkg);
        docComments = new DocCommentsTemplateData(context, pkg.getTrailingDocComments());
        for (AstNode packageSymbol : packageSymbols)
            this.packageSymbols.add(SymbolTemplateDataCreator.createData(context, packageSymbol));
    }

    public String getJsDirectory()
    {
        return jsDirectory;
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public Iterable<SymbolTemplateData> getPackageSymbols()
    {
        return packageSymbols;
    }

    private final String jsDirectory;
    private final SymbolTemplateData symbol;
    private final DocCommentsTemplateData docComments;
    // we want to have symbols in the order of definition in ToC
    private final List<SymbolTemplateData> packageSymbols = new ArrayList<SymbolTemplateData>();
}
