package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.DocumentableAstNode;
import zserio.emit.common.ZserioEmitException;

public class DocTemplateData
{
    public DocTemplateData(TemplateDataContext context, DocumentableAstNode astNode, String name,
            LinkedType linkedType)
            throws ZserioEmitException
    {
        this.context = context;

        this.docComment = new DocCommentTemplateData(astNode.getDocComment());

        this.name = name;

        this.anchorName = new LinkedType(astNode).getHyperlinkName();

        this.linkedType = linkedType;

        this.collaborationDiagramSvgUrl = context.getWithSvgDiagrams()
                ? DocEmitterTools.getTypeCollaborationSvgUrl(context.getOutputPath(), astNode)
                : null;

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        for (AstNode usedByNode : usedByCollector.getUsedByTypes(astNode, AstNode.class))
            usedByList.add(new LinkedType(usedByNode));
    }

    public TemplateDataContext getContext()
    {
        return context;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docComment;
    }

    public String getName()
    {
        return name;
    }

    public String getAnchorName()
    {
        return anchorName;
    }

    public LinkedType getLinkedType()
    {
        return linkedType;
    }

    public String getCollaborationDiagramSvgUrl()
    {
        return collaborationDiagramSvgUrl;
    }

    public Iterable<LinkedType> getUsedByList()
    {
        return usedByList;
    }

    private final TemplateDataContext context;

    private final DocCommentTemplateData docComment;
    private final String name;
    private final String anchorName;
    private final LinkedType linkedType;

    private final String collaborationDiagramSvgUrl;
    private final List<LinkedType> usedByList= new ArrayList<LinkedType>();
}
