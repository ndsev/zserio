package zserio.extension.doc;

import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base FreeMarker template data for all symbols in the package used by Package emitter.
 */
public class PackageTemplateDataBase
{
    public PackageTemplateDataBase(PackageTemplateDataContext context, DocumentableAstNode astNode)
            throws ZserioExtensionException
    {
        this.docComments = new DocCommentsTemplateData(context, astNode.getDocComments());
        symbol = SymbolTemplateDataCreator.createData(context, astNode);

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        final boolean svgCollaborationExists =
                SymbolCollaborationDotEmitter.svgSymbolCollaborationDiagramExists(
                        astNode, usedByCollector, context.getWithSvgDiagrams());
        this.collaborationDiagramSvg = (svgCollaborationExists) ?
                SymbolCollaborationDotEmitter.getSvgSymbolCollaborationHtmlLink(astNode,
                        context.getSymbolCollaborationDirectory()) : null;

        usedBySymbols = new TreeSet<SymbolTemplateData>();
        for (AstNode usedByNode : usedByCollector.getUsedByTypes(astNode))
        {
            usedBySymbols.add(
                    SymbolTemplateDataCreator.createTemplateInstantiationReferenceData(context, usedByNode));
        }
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public SymbolTemplateData getSymbol()
    {
        return symbol;
    }

    public String getCollaborationDiagramSvg()
    {
        return collaborationDiagramSvg;
    }

    public Iterable<SymbolTemplateData> getUsedBySymbols()
    {
        return usedBySymbols;
    }

    private final DocCommentsTemplateData docComments;
    private final SymbolTemplateData symbol;
    private final String collaborationDiagramSvg;
    private final SortedSet<SymbolTemplateData> usedBySymbols;
}
