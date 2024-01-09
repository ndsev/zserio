package zserio.ast;

import java.util.List;

/**
 * AST node for Pub/Sub messages.
 */
public final class PubsubMessage extends DocumentableAstNode implements ScopeSymbol
{
    /**
     * Constructor.
     *
     * @param location            AST node location.
     * @param name                Name of the Pub/Sub message.
     * @param typeReference       Reference to the message type.
     * @param topicDefinitionExpr Expression which defines the message's topic.
     * @param isPublished         Whether the message is published.
     * @param isSubscribed        Whether the message is subscribed.
     * @param docComments         List of documentation comments belonging to this node.
     */
    public PubsubMessage(AstLocation location, String name, TypeReference typeReference,
            Expression topicDefinitionExpr, boolean isPublished, boolean isSubscribed,
            List<DocComment> docComments)
    {
        super(location, docComments);

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

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Gets the reference to message type.
     *
     * @return The reference to message type.
     */
    public TypeReference getTypeReference()
    {
        return typeReference;
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
     * @return True when the message should be published, false otherwise.
     */
    public boolean isPublished()
    {
        return isPublished;
    }

    /**
     * Gets whether the message should be subscribed.
     *
     * @return True when the message should be subscribed, false otherwise.
     */
    public boolean isSubscribed()
    {
        return isSubscribed;
    }

    /**
     * Checks the message.
     */
    void check()
    {
        final ZserioType referencedBaseType = typeReference.getBaseTypeReference().getType();
        if (!(referencedBaseType instanceof CompoundType) && !(referencedBaseType instanceof BytesType))
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types or bytes can be used in pubsub messages, "
                            + "'" + typeReference.getReferencedTypeName() + "' is not a compound type!");
        }

        if (referencedBaseType instanceof CompoundType)
        {
            final CompoundType compoundType = (CompoundType)referencedBaseType;
            if (compoundType.getTypeParameters().size() > 0)
            {
                throw new ParserException(typeReference,
                        "Only non-parameterized compound types or bytes can be used in pubsub messages, '" +
                                ZserioTypeUtil.getReferencedFullName(typeReference) +
                                "' is a parameterized type!");
            }

            if (compoundType instanceof SqlTableType)
            {
                throw new ParserException(typeReference,
                        "SQL table '" + ZserioTypeUtil.getReferencedFullName(typeReference) +
                                "' cannot be used in pubsub messages!");
            }

            if (topicDefinitionExpr.getStringValue() == null)
                throw new ParserException(topicDefinitionExpr, "Topic definition must be a constant string!");
        }
    }

    private final String name;
    private final TypeReference typeReference;
    private final Expression topicDefinitionExpr;
    private final boolean isPublished;
    private final boolean isSubscribed;
}
