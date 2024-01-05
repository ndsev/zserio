package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for pubsubs in the package used by Package emitter.
 */
public final class PubsubTemplateData extends PackageTemplateDataBase
{
    public PubsubTemplateData(PackageTemplateDataContext context, PubsubType pubsubType)
            throws ZserioExtensionException
    {
        super(context, pubsubType);

        for (PubsubMessage message : pubsubType.getMessageList())
            messageList.add(new MessageTemplateData(context, pubsubType, message));
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public static final class MessageTemplateData
    {
        public MessageTemplateData(PackageTemplateDataContext context, PubsubType pubsubType,
                PubsubMessage pubsubMessage) throws ZserioExtensionException
        {
            keyword = pubsubMessage.isPublished() && pubsubMessage.isSubscribed() ? "pubsub"
                    : pubsubMessage.isPublished()
                    ? "publish"
                    : "subscribe";
            symbol = SymbolTemplateDataCreator.createData(context, pubsubType, pubsubMessage);
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            topicDefinition = docExpressionFormatter.formatGetter(pubsubMessage.getTopicDefinitionExpr());
            typeSymbol = SymbolTemplateDataCreator.createData(context, pubsubMessage.getTypeReference());
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

    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
}
