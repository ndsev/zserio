package zserio.ast;

import java.math.BigInteger;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.ParserException;

/**
 * AST node for built-in signed and unsigned variable integer types.
 *
 * Variable integer types (varint16, varuint16, ...) are Zserio types as well.
 */
public class VarIntegerType extends IntegerType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitVarIntegerType(this);
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

    /**
     * Gets the maximum number of bits the variable integer can occupy.
     *
     * @return Maximum bit size of this variable integer.
     */
    public int getMaxBitSize()
    {
        return maxBitSize;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        switch (getType())
        {
        case ZserioParserTokenTypes.VARUINT16:
            isSigned = false;
            maxBitSize = 16;
            upperBound = BigInteger.ONE.shiftLeft(15).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParserTokenTypes.VARINT16:
            upperBound = BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            isSigned = true;
            maxBitSize = 16;
            break;

        case ZserioParserTokenTypes.VARUINT32:
            isSigned = false;
            maxBitSize = 32;
            upperBound = BigInteger.ONE.shiftLeft(29).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParserTokenTypes.VARINT32:
            upperBound = BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            isSigned = true;
            maxBitSize = 32;
            break;

        case ZserioParserTokenTypes.VARUINT64:
            isSigned = false;
            maxBitSize = 64;
            upperBound = BigInteger.ONE.shiftLeft(57).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParserTokenTypes.VARINT64:
            upperBound = BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            isSigned = true;
            maxBitSize = 64;
            break;

        case ZserioParserTokenTypes.VARUINT:
            upperBound = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            isSigned = false;
            maxBitSize = 72;
            break;

        case ZserioParserTokenTypes.VARINT:
            upperBound = BigInteger.valueOf(Long.MAX_VALUE);
            lowerBound = BigInteger.valueOf(Long.MIN_VALUE);
            isSigned = true;
            maxBitSize = 72;
            break;

        default:
            throw new ParserException(this, "Unexpected AST node type in VarIntegerType!");
        }
    }

    private static final long serialVersionUID = -1494389523051889417L;

    private BigInteger upperBound;
    private BigInteger lowerBound;
    private boolean isSigned;
    private int maxBitSize;
}
