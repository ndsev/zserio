package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;

public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioEmitException
    {
        super(context, pubsubType);

        final JavaNativeMapper pythonTypeMapper = context.getJavaNativeMapper();

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublishing = false;
        boolean hasSubscribing = false;
        for (PubsubMessage message : messageList)
        {
            final MessageTemplateData templateData = new MessageTemplateData(pythonTypeMapper, message);
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
        public MessageTemplateData(JavaNativeMapper typeMapper, PubsubMessage message)
                throws ZserioEmitException
        {
            name = message.getName();
            topicDefinition = message.getTopicDefinition();
            final JavaNativeType pythonType = typeMapper.getJavaType(message.getType());
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
