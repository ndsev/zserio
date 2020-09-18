package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.CompoundType;
import zserio.emit.common.ZserioEmitException;

public class DocTemplateData
{
    public DocTemplateData(TemplateDataContext context, AstNode astNode) throws ZserioEmitException
    {
        this.context = context;
        this.collaborationDiagramSvgUrl = context.getWithSvgDiagrams()
                ? DocEmitterTools.getTypeCollaborationSvgUrl(context.getOutputPath(), astNode)
                : null;

        final UsedByCollector usedByCollector = context.getUsedByCollector();
        for (CompoundType compoundType : usedByCollector.getUsedByTypes(astNode, CompoundType.class))
            usedByTypes.add(new LinkedType(compoundType));
    }

    public TemplateDataContext getContext()
    {
        return context;
    }

    public String getCollaborationDiagramSvgUrl()
    {
        return collaborationDiagramSvgUrl;
    }

    public Iterable<LinkedType> getUsedByTypes()
    {
        return usedByTypes;
    }

    private final TemplateDataContext context;
    private final String collaborationDiagramSvgUrl;
    private final List<LinkedType> usedByTypes = new ArrayList<LinkedType>();
}
