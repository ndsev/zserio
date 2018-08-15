package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.ParserException;

/**
 * AST node for float types.
 *
 * Float types are Zserio types as well.
 */
public class FloatType extends BuiltInType implements FixedSizeType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitFloatType(this);
    }

    @Override
    public int getBitSize()
    {
        return bitSize;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        switch (getType())
        {
        case ZserioParserTokenTypes.FLOAT16:
            bitSize = 16;
            break;

        case ZserioParserTokenTypes.FLOAT32:
            bitSize = 32;
            break;

        case ZserioParserTokenTypes.FLOAT64:
            bitSize = 64;
            break;

        default:
            throw new ParserException(this, "Unexpected AST node type in FloatType!");
        }
    }

    private static final long serialVersionUID = 125193189598509024L;

    private int bitSize;
}
