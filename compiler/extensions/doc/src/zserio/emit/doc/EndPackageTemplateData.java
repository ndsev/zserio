package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;

public class EndPackageTemplateData
{
    public EndPackageTemplateData(TemplateDataContext context, Package pkg,
            List<AstNode> packageSymbols) throws ZserioEmitException
    {
        symbol = SymbolTemplateDataCreator.createData(context, pkg);
        docComments = new DocCommentsTemplateData(context, pkg.getTrailingDocComments());
        for (AstNode packageSymbol : packageSymbols)
            this.packageSymbols.add(new PackageSymbolTemplateData(context, packageSymbol));
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public Iterable<PackageSymbolTemplateData> getPackageSymbols()
    {
        return packageSymbols;
    }

    private final SymbolTemplateData symbol;
    private final DocCommentsTemplateData docComments;
    // we want to have symbols in the order of definition in ToC
    private final List<PackageSymbolTemplateData> packageSymbols = new ArrayList<PackageSymbolTemplateData>();
}
