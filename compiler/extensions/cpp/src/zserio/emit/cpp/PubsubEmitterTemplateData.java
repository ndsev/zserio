package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;

public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioEmitException
    {
        super(context, pubsubType);

        final CppNativeMapper cppTypeMapper = context.getCppNativeMapper();

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublifications = false;
        boolean hasSubscriptions = false;
        for (PubsubMessage message : messageList)
        {
            addHeaderIncludesForType(cppTypeMapper.getCppType(message.getType()));
            final MessageTemplateData templateData = new MessageTemplateData(cppTypeMapper, message);
            hasPublifications |= templateData.getIsPublished();
            hasSubscriptions |= templateData.getIsSubscribed();
            this.messageList.add(templateData);
        }
        this.hasPublifications = hasPublifications;
        this.hasSubscriptions = hasSubscriptions;
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public boolean getHasPublifications()
    {
        return hasPublifications;
    }

    public boolean getHasSubscriptions()
    {
        return hasSubscriptions;
    }

    public static class MessageTemplateData
    {
        public MessageTemplateData(CppNativeMapper typeMapper, PubsubMessage message) throws ZserioEmitException
        {
            name = message.getName();
            topicDefinition = message.getTopicDefinition();
            typeFullName = typeMapper.getCppType(message.getType()).getFullName();
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
    private final boolean hasPublifications;
    private final boolean hasSubscriptions;
}
