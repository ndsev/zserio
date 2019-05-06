package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioParser;

/**
 * AST node for Float types.
 *
 * Float types are Zserio types as well.
 */
public class FloatType extends BuiltInType implements FixedSizeType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public FloatType(Token token)
    {
        super(token);

        switch (token.getType())
        {
        case ZserioParser.FLOAT16:
            bitSize = 16;
            break;

        case ZserioParser.FLOAT32:
            bitSize = 32;
            break;

        case ZserioParser.FLOAT64:
            bitSize = 64;
            break;

        default:
            throw new InternalError("Unexpected AST node type in FloatType!");
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitFloatType(this);
    }

    @Override
    public int getBitSize()
    {
        return bitSize;
    }

    private final int bitSize;
}
