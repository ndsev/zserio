package zserio.emit.doc;

import zserio.ast.Import;
import zserio.ast.PackageSymbol;
import zserio.emit.common.ZserioEmitException;

public class ImportTemplateData
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
