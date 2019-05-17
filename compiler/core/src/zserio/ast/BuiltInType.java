package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * AST abstract node for all built-in types.
 *
 * This is an abstract class for all built-in Zserio types (boolean, float16, string, ...).
 */
public abstract class BuiltInType extends AstNodeBase implements ZserioType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public BuiltInType(Token token)
    {
        super(token);
        this.name = token.getText();
    }

    @Override
    public Package getPackage()
    {
        // built-in types do not have any package
        throw new InternalError("BuiltInType.getPackage() is not implemented!");
    }

    @Override
    public String getName()
    {
        return name;
    }

    private final String name;
}
