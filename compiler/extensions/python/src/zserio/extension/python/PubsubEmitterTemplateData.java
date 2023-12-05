package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for PubsubEmitter.
 */
public final class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioExtensionException
    {
        super(context, pubsubType, pubsubType);

        importPackage("typing");
        importPackage("zserio");

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublishing = false;
        boolean hasSubscribing = false;
        for (PubsubMessage message : messageList)
        {
            final MessageTemplateData templateData = new MessageTemplateData(context, message, this);
            hasPublishing |= templateData.getIsPublished();
            hasSubscribing |= templateData.getIsSubscribed();
            this.messageList.add(templateData);
        }
        this.hasPublishing = hasPublishing;
        this.hasSubscribing = hasSubscribing;
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public boolean getHasPublishing()
    {
        return hasPublishing;
    }

    public boolean getHasSubscribing()
    {
        return hasSubscribing;
    }

    public static final class MessageTemplateData
    {
        public MessageTemplateData(TemplateDataContext context, PubsubMessage message,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
            final ExpressionFormatter pythonExpressionFormatter =
                    context.getPythonExpressionFormatter(importCollector);
            name = message.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            final TypeReference messageTypeReference = message.getTypeReference();
            final PythonNativeType pythonType = pythonNativeMapper.getPythonType(messageTypeReference);
            importCollector.importType(pythonType);
            typeInfo = new NativeTypeInfoTemplateData(pythonType, messageTypeReference);
            topicDefinition = pythonExpressionFormatter.formatGetter(message.getTopicDefinitionExpr());
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
            docComments = DocCommentsDataCreator.createData(context, message);
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getTopicDefinition()
        {
            return topicDefinition;
        }

        public boolean getIsPublished()
        {
            return isPublished;
        }

        public boolean getIsSubscribed()
        {
            return isSubscribed;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String snakeCaseName;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String topicDefinition;
        private final boolean isPublished;
        private final boolean isSubscribed;
        private final DocCommentsTemplateData docComments;
    }

    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final boolean hasPublishing;
    private final boolean hasSubscribing;
}
