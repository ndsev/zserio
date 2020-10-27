package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class PubsubTemplateData extends HtmlTemplateData
{
    public PubsubTemplateData(TemplateDataContext context, PubsubType pubsubType) throws ZserioEmitException
    {
        super(context, pubsubType);

        packageName = AstNodePackageNameMapper.getPackageName(pubsubType).toString();
        for (PubsubMessage message : pubsubType.getMessageList())
            messageList.add(new MessageTemplateData(context, pubsubType, message));
    }

    public String getPackageName()
    {
        return packageName;
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public static class MessageTemplateData
    {
        public MessageTemplateData(TemplateDataContext context, PubsubType pubsubType,
                PubsubMessage pubsubMessage) throws ZserioEmitException
        {
            keyword = pubsubMessage.isPublished() && pubsubMessage.isSubscribed() ?
                    "pubsub" : pubsubMessage.isPublished() ? "publish" : "subscribe";
            symbol = SymbolTemplateDataCreator.createData(context, pubsubType, pubsubMessage);
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            topicDefinition = docExpressionFormatter.formatGetter(pubsubMessage.getTopicDefinitionExpr());
            typeSymbol = SymbolTemplateDataCreator.createData(context, pubsubMessage.getType());
            docComments = new DocCommentsTemplateData(context, pubsubMessage.getDocComments());
        }

        public String getKeyword()
        {
            return keyword;
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public String getTopicDefinition()
        {
            return topicDefinition;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String keyword;
        private final SymbolTemplateData symbol;
        private final String topicDefinition;
        private final SymbolTemplateData typeSymbol;
        private final DocCommentsTemplateData docComments;
    }

    private final String packageName;
    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
}
