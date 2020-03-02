package zserio.emit.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.python.types.PythonNativeType;

public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioEmitException
    {
        super(context, pubsubType);

        final PythonNativeMapper pythonTypeMapper = context.getPythonNativeMapper();

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublifications = false;
        boolean hasSubscriptions = false;
        for (PubsubMessage message : messageList)
        {
            final MessageTemplateData templateData = new MessageTemplateData(pythonTypeMapper, message, this);
            hasPublifications |= templateData.getIsPublished();
            hasSubscriptions |= templateData.getIsSubscribed();
            this.messageList.add(templateData);
        }
        this.hasPublifications = hasPublifications;
        this.hasSubscriptions = hasSubscriptions;

        importPackage("zserio");
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
        public MessageTemplateData(PythonNativeMapper typeMapper, PubsubMessage message,
                ImportCollector importCollector) throws ZserioEmitException
        {
            name = message.getName();
            topicDefinition = message.getTopicDefinition();
            final PythonNativeType pythonType = typeMapper.getPythonType(message.getType());
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
    private final boolean hasPublifications;
    private final boolean hasSubscriptions;
}
