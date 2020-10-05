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

        final SymbolTemplateData symbol = SymbolTemplateDataCreator.createData(context, astNode);
        this.anchorName = symbol.getHtmlLink().getHtmlAnchor();
        this.collaborationDiagramSvg = (context.getWithSvgDiagrams()) ?
                SymbolCollaborationDotEmitter.getSvgCollaborationHtmlLink(astNode, context.getPackageMapper(),
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

    public String getName()
    {
        return name;
    }

    public String getAnchorName()
    {
        return anchorName;
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
    private final String name;
    private final String anchorName;

    private final String collaborationDiagramSvg;
    private final List<SymbolTemplateData> usedByList;
}
