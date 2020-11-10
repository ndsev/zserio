package zserio.emit.doc;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.emit.common.ZserioEmitException;

public class HtmlTemplateData
{
    public HtmlTemplateData(TemplateDataContext context, DocumentableAstNode astNode) throws ZserioEmitException
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
            usedBySymbols.add(createSymbol(context, usedByNode));
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

    // TODO[Mi-L@]: This same logic is used on several places. Improve!
    //              See e.g. SymbolCollaborationDotTemplateData.
    private SymbolTemplateData createSymbol(TemplateDataContext context, AstNode node)
    {
        AstNode symbolNode = node;
        // use instantitiation reference instead of instantiation to get template with it's argument
        if (symbolNode instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType instance = (ZserioTemplatableType)symbolNode;
            final ZserioTemplatableType template = instance.getTemplate();
            if (template != null)
            {
                final Iterator<TypeReference> instantiationReferenceIterator =
                        instance.getInstantiationReferenceStack().iterator();
                if (instantiationReferenceIterator.hasNext())
                    symbolNode = instantiationReferenceIterator.next();
            }
        }

        return SymbolTemplateDataCreator.createData(context, symbolNode);
    }

    private final DocCommentsTemplateData docComments;
    private final SymbolTemplateData symbol;
    private final String collaborationDiagramSvg;
    private final SortedSet<SymbolTemplateData> usedBySymbols;
}
