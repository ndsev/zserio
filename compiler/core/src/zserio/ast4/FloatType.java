package zserio.ast4;

import org.antlr.v4.runtime.Token;

import zserio.antlr.Zserio4Parser;

/**
 * AST node for float types.
 *
 * Float types are Zserio types as well.
 */
public class FloatType extends BuiltInType implements FixedSizeType
{
    public FloatType(Token token)
    {
        super(token);

        switch (token.getType())
        {
        case Zserio4Parser.FLOAT16:
            bitSize = 16;
            break;
        case Zserio4Parser.FLOAT32:
            bitSize = 32;
            break;
        case Zserio4Parser.FLOAT64:
            bitSize = 64;
            break;
        default:
            throw new ParserException(this, "Unexpected AST node type in FloatType!");
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

    private int bitSize;
}
