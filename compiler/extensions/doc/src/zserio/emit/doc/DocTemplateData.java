package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.emit.common.ZserioEmitException;

public class DocTemplateData
{
    public DocTemplateData(TemplateDataContext context, DocumentableAstNode astNode, String name)
            throws ZserioEmitException
    {
        this.docComments = new DocCommentsTemplateData(astNode.getDocComments());
        this.name = name;
        this.url = DocEmitterTools.getUrlNameFromType(astNode);
        this.anchorName = DocEmitterTools.getAnchorName(astNode);
        this.collaborationDiagramSvgUrl = context.getWithSvgDiagrams()
                ? DocEmitterTools.getTypeCollaborationSvgUrl(context.getOutputPath(), astNode)
                : null;

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        final SymbolTemplateDataMapper symbolTemplateDataMapper = context.getSymbolTemplateDataMapper();
        usedByList = new ArrayList<SymbolTemplateData>();
        for (AstNode usedByNode : usedByCollector.getUsedByTypes(astNode, AstNode.class))
            usedByList.add(symbolTemplateDataMapper.getSymbol(usedByNode));
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public String getName()
    {
        return name;
    }

    public String getAnchorName()
    {
        return anchorName;
    }

    public String getUrl()
    {
        return url;
    }

    public String getCollaborationDiagramSvgUrl()
    {
        return collaborationDiagramSvgUrl;
    }

    public Iterable<SymbolTemplateData> getUsedByList()
    {
        return usedByList;
    }

    private final DocCommentsTemplateData docComments;
    private final String name;
    private final String url;
    private final String anchorName;

    private final String collaborationDiagramSvgUrl;
    private final List<SymbolTemplateData> usedByList;
}
