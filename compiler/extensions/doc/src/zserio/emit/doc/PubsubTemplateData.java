package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;

public class PubsubTemplateData
{
    public PubsubTemplateData(PubsubType pubsubType, String outputPath, boolean withSvgDiagrams)
            throws ZserioEmitException
    {
        name = pubsubType.getName();
        packageName = pubsubType.getPackage().getPackageName().toString();
        docComment = new DocCommentTemplateData(pubsubType.getDocComment());
        for (PubsubMessage message : pubsubType.getMessageList())
        {
            messageList.add(new MessageTemplateData(message));
        }
        collaborationDiagramSvgFileName = (withSvgDiagrams)
                ? DocEmitterTools.getTypeCollaborationSvgUrl(outputPath, pubsubType) : null;
    }

    public String getName()
    {
        return name;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public DocCommentTemplateData getDocComment()
    {
        return docComment;
    }

    public Iterable<MessageTemplateData> getMessageList()
    {
        return messageList;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return collaborationDiagramSvgFileName;
    }

    public static class MessageTemplateData
    {
        public MessageTemplateData(PubsubMessage pubsubMessage) throws ZserioEmitException
        {
            keyword = pubsubMessage.isPublished() && pubsubMessage.isSubscribed() ?
                    "pubsub" : pubsubMessage.isPublished() ? "publish" : "subscribe";
            name = pubsubMessage.getName();
            topicDefinition = pubsubMessage.getTopicDefinition();
            type = new LinkedType(pubsubMessage.getType());
            docComment = new DocCommentTemplateData(pubsubMessage.getDocComment());
        }

        public String getKeyword()
        {
            return keyword;
        }

        public String getName()
        {
            return name;
        }

        public String getTopicDefinition()
        {
            return topicDefinition;
        }

        public LinkedType getType()
        {
            return type;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        private final String keyword;
        private final String name;
        private final String topicDefinition;
        private final LinkedType type;
        private final DocCommentTemplateData docComment;
    }

    private final String name;
    private final String packageName;
    private final DocCommentTemplateData docComment;
    private final List<MessageTemplateData> messageList = new ArrayList<MessageTemplateData>();
    private final String collaborationDiagramSvgFileName;
}