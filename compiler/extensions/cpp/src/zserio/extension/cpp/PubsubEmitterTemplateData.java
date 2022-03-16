package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for PubsubEmitter.
 */
public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioExtensionException
    {
        super(context, pubsubType);

        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(this);

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublishing = false;
        boolean hasSubscribing = false;
        for (PubsubMessage message : messageList)
        {
            addHeaderIncludesForType(cppNativeMapper.getCppType(message.getTypeReference()));
            final MessageTemplateData templateData = new MessageTemplateData(cppNativeMapper,
                    cppExpressionFormatter, message);
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
        public MessageTemplateData(CppNativeMapper cppNativeMapper, ExpressionFormatter cppExpressionFormatter,
                PubsubMessage message) throws ZserioExtensionException
        {
            name = message.getName();
            final TypeReference messageTypeReference = message.getTypeReference();
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(messageTypeReference);
            typeInfo = new NativeTypeInfoTemplateData(cppNativeType, messageTypeReference);
            topicDefinition = cppExpressionFormatter.formatGetter(message.getTopicDefinitionExpr());
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
        }

        public String getName()
        {
            return name;
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

        private final String name;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String topicDefinition;
        private final boolean isPublished;
        private final boolean isSubscribed;
    }

    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final boolean hasPublishing;
    private final boolean hasSubscribing;
}
