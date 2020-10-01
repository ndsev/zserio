package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class PubsubTemplateData
{
    public PubsubTemplateData(TemplateDataContext context, PubsubType pubsubType) throws ZserioEmitException
    {
        name = pubsubType.getName();
        packageName = pubsubType.getPackage().getPackageName().toString();
        anchorName = DocEmitterTools.getAnchorName(pubsubType);
        docComments = new DocCommentsTemplateData(pubsubType.getDocComments());
        for (PubsubMessage message : pubsubType.getMessageList())
        {
            messageList.add(new MessageTemplateData(pubsubType, context.getExpressionFormatter(), message,
                    context.getSymbolTemplateDataMapper()));
        }
        collaborationDiagramSvgFileName = (context.getWithSvgDiagrams())
                ? DocEmitterTools.getTypeCollaborationSvgUrl(context.getOutputPath(), pubsubType) : null;
    }

    public String getName()
    {
        return name;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getAnchorName()
    {
        return anchorName;
    }

    public DocCommentsTemplateData getDocComments()
    {
        return docComments;
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return collaborationDiagramSvgFileName;
    }

    public static class MessageTemplateData
    {
        public MessageTemplateData(PubsubType pubsubType, ExpressionFormatter docExpressionFormatter,
                PubsubMessage pubsubMessage, SymbolTemplateDataMapper symbolTemplateDataMapper)
                        throws ZserioEmitException
        {
            keyword = pubsubMessage.isPublished() && pubsubMessage.isSubscribed() ?
                    "pubsub" : pubsubMessage.isPublished() ? "publish" : "subscribe";
            name = pubsubMessage.getName();
            anchorName = DocEmitterTools.getAnchorName(pubsubType, name);
            topicDefinition = docExpressionFormatter.formatGetter(pubsubMessage.getTopicDefinitionExpr());
            symbol = symbolTemplateDataMapper.getSymbol(pubsubMessage.getType());
            docComments = new DocCommentsTemplateData(pubsubMessage.getDocComments());
        }

        public String getKeyword()
        {
            return keyword;
        }

        public String getName()
        {
            return name;
        }

        public String getAnchorName()
        {
            return anchorName;
        }

        public String getTopicDefinition()
        {
            return topicDefinition;
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String keyword;
        private final String name;
        private final String anchorName;
        private final String topicDefinition;
        private final SymbolTemplateData symbol;
        private final DocCommentsTemplateData docComments;
    }

    private final String name;
    private final String packageName;
    private final String anchorName;
    private final DocCommentsTemplateData docComments;
    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final String collaborationDiagramSvgFileName;
}
