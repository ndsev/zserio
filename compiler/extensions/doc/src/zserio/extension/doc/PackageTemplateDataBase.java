package zserio.extension.doc;

import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base FreeMarker template data for all symbols in the package used by Package emitter.
 */
public class PackageTemplateDataBase extends ContentTemplateDataBase
{
    public PackageTemplateDataBase(PackageTemplateDataContext context, DocumentableAstNode astNode)
            throws ZserioExtensionException
    {
        super(context, astNode);

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        final boolean svgCollaborationExists =
                SymbolCollaborationDotEmitter.svgSymbolCollaborationDiagramExists(
                        astNode, usedByCollector, context.getWithSvgDiagrams());
        this.collaborationDiagramSvg = (svgCollaborationExists) ?
                SymbolCollaborationDotEmitter.getSvgSymbolCollaborationHtmlLink(astNode,
                        context.getSymbolCollaborationDirectory()) : null;

        usedBySymbols = new TreeSet<SymbolTemplateData>();
        for (AstNode usedByNode : usedByCollector.getUsedBySymbols(astNode))
        {
            usedBySymbols.add(
                    SymbolTemplateDataCreator.createTemplateInstantiationReferenceData(context, usedByNode));
        }
    }

    public String getCollaborationDiagramSvg()
    {
        return collaborationDiagramSvg;
    }

    public Iterable<SymbolTemplateData> getUsedBySymbols()
    {
        return usedBySymbols;
    }

    private final String collaborationDiagramSvg;
    private final SortedSet<SymbolTemplateData> usedBySymbols;
}
