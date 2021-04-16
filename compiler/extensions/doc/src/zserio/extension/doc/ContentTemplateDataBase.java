package zserio.extension.doc;

import zserio.ast.DocumentableAstNode;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base FreeMarker template data for all symbols in the package used by content emitters.
 */
public class ContentTemplateDataBase
{
    public ContentTemplateDataBase(ContentTemplateDataContext context, DocumentableAstNode astNode)
            throws ZserioExtensionException
    {
        this.docComments = new DocCommentsTemplateData(context, astNode.getDocComments());
        symbol = SymbolTemplateDataCreator.createData(context, astNode);
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    private final DocCommentsTemplateData docComments;
    private final SymbolTemplateData symbol;
}
