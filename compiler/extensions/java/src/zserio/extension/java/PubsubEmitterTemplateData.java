package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for PubsubEmitter.
 */
public class PubsubEmitterTemplateData extends UserTypeTemplateData
{
    public PubsubEmitterTemplateData(TemplateDataContext context, PubsubType pubsubType)
            throws ZserioExtensionException
    {
        super(context, pubsubType, pubsubType.getDocComments());

        Iterable<PubsubMessage> messageList = pubsubType.getMessageList();
        boolean hasPublishing = false;
        boolean hasSubscribing = false;
        for (PubsubMessage message : messageList)
        {
            final MessageTemplateData templateData = new MessageTemplateData(context, message);
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
        public MessageTemplateData(TemplateDataContext context, PubsubMessage message)
                throws ZserioExtensionException
        {
            final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
            final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
            name = message.getName();
            final TypeReference referencedType = message.getTypeReference();
            final JavaNativeType javaType = javaNativeMapper.getJavaType(referencedType);
            typeInfo = new NativeTypeInfoTemplateData(javaType, referencedType);
            topicDefinition = javaExpressionFormatter.formatGetter(message.getTopicDefinitionExpr());
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
            final List<DocComment> messageDocComments = message.getDocComments();
            docComments = messageDocComments.isEmpty() ? null : new DocCommentsTemplateData(messageDocComments);
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
