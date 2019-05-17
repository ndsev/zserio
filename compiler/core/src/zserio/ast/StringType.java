package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * AST node for String types.
 *
 * String types (Zserio keyword 'string') are Zserio types as well.
 */
public class StringType extends BuiltInType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public StringType(Token token)
    {
        super(token);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitStringType(this);
    }
}
