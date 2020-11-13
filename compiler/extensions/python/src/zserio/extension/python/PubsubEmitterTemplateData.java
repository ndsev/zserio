package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioExtensionException
    {
        super(context, pubsubType);

        importPackage("typing");
        importPackage("zserio");

        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublishing = false;
        boolean hasSubscribing = false;
        for (PubsubMessage message : messageList)
        {
            final MessageTemplateData templateData = new MessageTemplateData(pythonNativeMapper,
                    pythonExpressionFormatter, message, this);
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

    public static class MessageTemplateData
    {
        public MessageTemplateData(PythonNativeMapper pythonNativeMapper,
                ExpressionFormatter pythonExpressionFormatter, PubsubMessage message,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            name = message.getName();
            topicDefinition = pythonExpressionFormatter.formatGetter(message.getTopicDefinitionExpr());
            final PythonNativeType pythonType = pythonNativeMapper.getPythonType(message.getType());
            importCollector.importType(pythonType);
            typeFullName = pythonType.getFullName();
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
        }

        public String getName()
        {
            return name;
        }

        public String getTopicDefinition()
        {
            return topicDefinition;
        }

        public String getTypeFullName()
        {
            return typeFullName;
        }

        public boolean getIsPublished()
        {
            return isPublished;
        }

        public boolean getIsSubscribed()
        {
            return isSubscribed;
        }

        private final String name;
        private final String topicDefinition;
        private final String typeFullName;
        private final boolean isPublished;
        private final boolean isSubscribed;
    }

    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final boolean hasPublishing;
    private final boolean hasSubscribing;
}
