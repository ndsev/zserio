package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * AST node for boolean types.
 *
 * Boolean types are Zserio types as well.
 */
public class BooleanType extends BuiltInType implements FixedSizeType
{
    public BooleanType(Token token)
    {
        super(token);
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitBooleanType(this);
    }

    @Override
    public int getBitSize()
    {
        return 1;
    }
}
