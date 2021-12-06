package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

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
            addHeaderIncludesForType(cppNativeMapper.getCppType(message.getType()));
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
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(message.getType());
            typeInfo = new TypeInfoTemplateData(message.getType(), cppNativeType);
            final String topicDefinitionStringValue = message.getTopicDefinitionExpr().getStringValue();
            if (topicDefinitionStringValue == null)
            {
                throw new ZserioExtensionException(
                        "Unexpected topic definition which is a non-constant string!");
            }
            topicDefinition = CppLiteralFormatter.formatStringLiteral(topicDefinitionStringValue);
            typeFullName = cppNativeType.getFullName();
            isPublished = message.isPublished();
            isSubscribed = message.isSubscribed();
        }

        public String getName()
        {
            return name;
        }

        public TypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
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
        private final TypeInfoTemplateData typeInfo;
        private final String topicDefinition;
        private final String typeFullName;
        private final boolean isPublished;
        private final boolean isSubscribed;
    }

    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final boolean hasPublishing;
    private final boolean hasSubscribing;
}
