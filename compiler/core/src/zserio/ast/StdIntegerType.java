package zserio.ast;

import java.math.BigInteger;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.ParserException;

/**
 * AST node for built-in signed and unsigned standard integer types.
 *
 * Standard integer types (int8, uint8, ...) are Zserio types as well.
 */
public class StdIntegerType extends FixedSizeIntegerType implements FixedSizeType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitStdIntegerType(this);
    }

    @Override
    public BigInteger getUpperBound()
    {
        return upperBound;
    }

    @Override
    public BigInteger getLowerBound()
    {
        return lowerBound;
    }

    @Override
    public boolean isSigned()
    {
        return isSigned;
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
        case ZserioParserTokenTypes.UINT8:
            isSigned = false;
            bitSize = 8;
            break;

        case ZserioParserTokenTypes.INT8:
            isSigned = true;
            bitSize = 8;
            break;

        case ZserioParserTokenTypes.UINT16:
            isSigned = false;
            bitSize = 16;
            break;

        case ZserioParserTokenTypes.INT16:
            isSigned = true;
            bitSize = 16;
            break;

        case ZserioParserTokenTypes.UINT32:
            isSigned = false;
            bitSize = 32;
            break;

        case ZserioParserTokenTypes.INT32:
            isSigned = true;
            bitSize = 32;
            break;

        case ZserioParserTokenTypes.UINT64:
            isSigned = false;
            bitSize = 64;
            break;

        case ZserioParserTokenTypes.INT64:
            isSigned = true;
            bitSize = 64;
            break;

        default:
            throw new ParserException(this, "Unexpected AST node type in StdIntegerType!");
        }

        if (isSigned)
        {
            upperBound = BigInteger.ONE.shiftLeft(bitSize - 1).subtract(BigInteger.ONE);
            lowerBound = upperBound.not();
        }
        else
        {
            upperBound = BigInteger.ONE.shiftLeft(bitSize).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
        }
    }

    private static final long serialVersionUID = 1419392195242487062L;

    private BigInteger upperBound;
    private BigInteger lowerBound;
    private boolean isSigned;
    private int bitSize;
}
