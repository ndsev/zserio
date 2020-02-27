package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * AST node for Pub/Sub types.
 *
 * Pub/Sub types are Zserio types as well.
 */
public class PubsubType extends DocumentableAstNode implements ZserioScopedType
{
    public PubsubType(AstLocation location, Package pkg, String name, List<PubsubMessage> messages,
            DocComment docComment)
    {
        super(location, docComment);

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

    private final Scope scope = new Scope(this);

    private final Package pkg;
    private final String name;
    private final List<PubsubMessage> messages;
}
