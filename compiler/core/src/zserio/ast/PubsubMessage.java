package zserio.ast;

/**
 * AST node for Pub/Sub messages.
 */
public class PubsubMessage extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location        	  AST node location.
     * @param name            	  Name of the Pub/Sub message.
     * @param typeReference   	  Reference to the message type.
     * @param topicDefinitionExpr Expression which defines the message's topic.
     * @param isPublished     	  Whether the message is published.
     * @param isSubscribed    	  Whether the message is subscribed.
     * @param docComment      	  Documentation comment belonging to this node.
     */
    public PubsubMessage(AstLocation location, String name, TypeReference typeReference,
            Expression topicDefinitionExpr, boolean isPublished, boolean isSubscribed, DocComment docComment)
    {
        super(location, docComment);

        this.name = name;
        this.typeReference = typeReference;
        this.topicDefinitionExpr = topicDefinitionExpr;
        this.isPublished = isPublished;
        this.isSubscribed = isSubscribed;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitPubsubMessage(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        topicDefinitionExpr.accept(visitor);
        typeReference.accept(visitor);
    }

    /**
     * Gets name of the Pub/Sub message.
     *
     * @return Pub/Sub message name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the message type.
     *
     * @return Type of the message.
     */
    public CompoundType getType()
    {
        return (CompoundType)typeReference.getBaseTypeReference().getType();
    }

    /**
     * Gets topic expression.
     *
     * @return Expression which defines the topic.
     */
    public Expression getTopicDefinitionExpr()
    {
        return topicDefinitionExpr;
    }

    /**
     * Gets whether the message should be published.
     *
     * @returns True when the message should be published, false otherwise.
     */
    public boolean isPublished()
    {
        return isPublished;
    }

    /**
     * Gets whether the message should be subscribed.
     *
     * @returns True when the message should be subscribed, false otherwise.
     */
    public boolean isSubscribed()
    {
        return isSubscribed;
    }

    /**
     * Checks the message type.
     */
    void check()
    {
        final ZserioType referencedBaseType = typeReference.getBaseTypeReference().getType();
        if (!(referencedBaseType instanceof CompoundType))
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in pubsub messages, " +
                    "'" + typeReference.getReferencedTypeName() + "' is not a compound type!");
        }

        final CompoundType compoundType = (CompoundType)referencedBaseType;
        if (compoundType.getTypeParameters().size() > 0)
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in pubsub messages, '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) + "' is a parameterized type!");
        }

        if (compoundType instanceof SqlTableType)
        {
            throw new ParserException(typeReference, "SQL table '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) +
                    "' cannot be used in pubsub messages!");
        }

        if (topicDefinitionExpr.getStringValue() == null)
            throw new ParserException(topicDefinitionExpr, "Topic definition must be a constant string!");
    }

    private final String name;
    private final TypeReference typeReference;
    private final Expression topicDefinitionExpr;
    private final boolean isPublished;
    private final boolean isSubscribed;
}
