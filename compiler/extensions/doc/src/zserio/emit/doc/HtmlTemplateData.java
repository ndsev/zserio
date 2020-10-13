package zserio.emit.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;

public class HtmlTemplateData
{
    public HtmlTemplateData(TemplateDataContext context, DocumentableAstNode astNode) throws ZserioEmitException
    {
        this.docComments = new DocCommentsTemplateData(context, astNode.getDocComments());
        symbol = SymbolTemplateDataCreator.createData(context, astNode);

        final PackageMapper packageMapper = context.getPackageMapper();
        final UsedByCollector usedByCollector = context.getUsedByCollector();
        final boolean svgCollaborationExists = SymbolCollaborationDotEmitter.svgSymbolCollaborationFileExists(astNode,
                usedByCollector, context.getWithSvgDiagrams());
        this.collaborationDiagramSvg = (svgCollaborationExists) ?
                SymbolCollaborationDotEmitter.getSvgSymbolCollaborationHtmlLink(astNode, packageMapper,
                        context.getSymbolCollaborationDirectory()) : null;

        usedByList = new ArrayList<SymbolTemplateData>();
        for (AstNode usedByNode : usedByCollector.getUsedByTypes(astNode, AstNode.class))
        {
            usedByList.add(createSymbol(context, usedByNode));
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

    public Iterable<SymbolTemplateData> getUsedByList()
    {
        return usedByList;
    }

    // TODO[Mi-L@]: This same logic is used on several places. Improve!
    //              See e.g. SymbolCollaborationDotTemplateData.
    private SymbolTemplateData createSymbol(TemplateDataContext context, AstNode node)
    {
        AstNode symbolNode = node;
        // use instantitiation reference instead of instantiation to get template with it's argument
        if (symbolNode instanceof ZserioTemplatableType)
        {
            ZserioTemplatableType instance = (ZserioTemplatableType)symbolNode;
            ZserioTemplatableType template = instance.getTemplate();
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
    private final List<SymbolTemplateData> usedByList;
}
