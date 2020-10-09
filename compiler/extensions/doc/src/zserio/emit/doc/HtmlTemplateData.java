package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;

public class HtmlTemplateData
{
    public HtmlTemplateData(TemplateDataContext context, DocumentableAstNode astNode) throws ZserioEmitException
    {
        this.docComments = new DocCommentsTemplateData(context, astNode.getDocComments());
        symbol = SymbolTemplateDataCreator.createData(context, astNode);

        final PackageMapper packageMapper = context.getPackageMapper();
        final boolean svgCollaborationExists = SymbolCollaborationDotEmitter.svgCollaborationFileExists(
                astNode, packageMapper, context.getOutputPathName());
        this.collaborationDiagramSvg = (context.getWithSvgDiagrams() && svgCollaborationExists) ?
                SymbolCollaborationDotEmitter.getSvgCollaborationHtmlLink(astNode, packageMapper,
                        context.getTypeCollaborationDirectory()) : null;

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        usedByList = new ArrayList<SymbolTemplateData>();
        for (AstNode usedByNode : usedByCollector.getUsedByTypes(astNode, AstNode.class))
        {
            usedByList.add(SymbolTemplateDataCreator.createData(context, usedByNode));
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

    private final DocCommentsTemplateData docComments;
    private final SymbolTemplateData symbol;
    private final String collaborationDiagramSvg;
    private final List<SymbolTemplateData> usedByList;
}
