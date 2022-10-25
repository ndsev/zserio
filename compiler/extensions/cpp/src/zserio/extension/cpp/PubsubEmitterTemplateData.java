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
        super(context, pubsubType, pubsubType);

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

    public static class MessageTemplateData
    {
        public MessageTemplateData(TemplateDataContext context, PubsubMessage message,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            name = message.getName();
            final TypeReference messageTypeReference = message.getTypeReference();
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(messageTypeReference);
            includeCollector.addHeaderIncludesForType(cppNativeType);
            typeInfo = new NativeTypeInfoTemplateData(cppNativeType, messageTypeReference);
            topicDefinition = cppExpressionFormatter.formatGetter(message.getTopicDefinitionExpr());
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
            docComments = DocCommentsDataCreator.createData(context, message);
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

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
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
