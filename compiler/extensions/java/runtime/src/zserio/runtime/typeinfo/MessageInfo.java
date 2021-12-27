package zserio.runtime.typeinfo;

/**
 * Type information for pubsub message.
 */
public class MessageInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Message schema name.
     * @param typeInfo Message type info.
     * @param isPublished Flag whether the message is published.
     * @param isSubscribed Flag whether the message is subscribed.
     * @param topic Message topic definition.
     */
    public MessageInfo(String schemaName, TypeInfo typeInfo,
            boolean isPublished, boolean isSubscribed, String topic)
    {
        this.schemaName = schemaName;
        this.typeInfo = typeInfo;
        this.isPublished = isPublished;
        this.isSubscribed = isSubscribed;
        this.topic = topic;
    }

    /**
     * Gets name of the message as is defined in zserio schema.
     *
     * @return Message schema name.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for a message type.
     *
     * @return Message type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    /**
     * Gets whether the message is published.
     *
     * @return True if the message is published, false otherwise.
     */
    public boolean isPublished()
    {
        return isPublished;
    }

    /**
     * Gets whether the message is subscribed.
     *
     * @return True if the message is subscribed, false otherwise.
     */
    public boolean isSubscribed()
    {
        return isSubscribed;
    }

    /**
     * Gets pubsub topic definition for the message.
     *
     * @return Topic definition.
     */
    public String getTopic()
    {
        return topic;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
    private final boolean isPublished;
    private final boolean isSubscribed;
    private final String topic;
}
