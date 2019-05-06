package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * AST node for Boolean types.
 *
 * Boolean types are Zserio types as well.
 */
public class BooleanType extends BuiltInType implements FixedSizeType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public BooleanType(Token token)
    {
        super(token);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBooleanType(this);
    }

    @Override
    public int getBitSize()
    {
        return 1;
    }
}
