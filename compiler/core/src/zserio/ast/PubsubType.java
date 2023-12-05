package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * AST node for Pub/Sub types.
 *
 * Pub/Sub types are Zserio types as well.
 */
public final class PubsubType extends DocumentableAstNode implements ZserioScopedType
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param pkg         Package to which belongs the Pub/Sub type.
     * @param name        Name of the Pub/Sub type.
     * @param messages    List of all messages which belong to the Pub/Sub type.
     * @param docComments List of documentation comments belonging to this node.
     */
    public PubsubType(AstLocation location, Package pkg, String name, List<PubsubMessage> messages,
            List<DocComment> docComments)
    {
        super(location, docComments);

        this.pkg = pkg;
        this.name = name;
        this.messages = messages;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitPubsubType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (PubsubMessage message : messages)
            message.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Scope getScope()
    {
        return scope;
    }

    /**
     * Gets list of pub/sub messages.
     *
     * @return List of messages.
     */
    public List<PubsubMessage> getMessageList()
    {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Checks the pubsub type.
     */
    void check()
    {
        final ScopeSymbolValidator validator = new ScopeSymbolValidator();
        for (PubsubMessage message : messages)
            validator.validate(message.getName(), message);
    }

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final String name;
    private final List<PubsubMessage> messages;
}
