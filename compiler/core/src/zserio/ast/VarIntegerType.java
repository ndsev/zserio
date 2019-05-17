package zserio.ast;

import java.math.BigInteger;

import org.antlr.v4.runtime.Token;

import zserio.antlr.ZserioParser;

/**
 * AST node for built-in signed and unsigned variable Integer types.
 *
 * Variable Integer types (varint16, varuint16, ...) are Zserio types as well.
 */
public class VarIntegerType extends IntegerType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public VarIntegerType(Token token)
    {
        super(token);

        switch (token.getType())
        {
        case ZserioParser.VARUINT16:
            isSigned = false;
            maxBitSize = 16;
            upperBound = BigInteger.ONE.shiftLeft(15).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParser.VARINT16:
            isSigned = true;
            maxBitSize = 16;
            upperBound = BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            break;

        case ZserioParser.VARUINT32:
            isSigned = false;
            maxBitSize = 32;
            upperBound = BigInteger.ONE.shiftLeft(29).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParser.VARINT32:
            isSigned = true;
            maxBitSize = 32;
            upperBound = BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            break;

        case ZserioParser.VARUINT64:
            isSigned = false;
            maxBitSize = 64;
            upperBound = BigInteger.ONE.shiftLeft(57).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParser.VARINT64:
            isSigned = true;
            maxBitSize = 64;
            upperBound = BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE);
            lowerBound = upperBound.negate();
            break;

        case ZserioParser.VARUINT:
            isSigned = false;
            maxBitSize = 72;
            upperBound = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
            break;

        case ZserioParser.VARINT:
            isSigned = true;
            maxBitSize = 72;
            upperBound = BigInteger.valueOf(Long.MAX_VALUE);
            lowerBound = BigInteger.valueOf(Long.MIN_VALUE);
            break;

        default:
            throw new InternalError("Unexpected AST node type in VarIntegerType!");
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
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

    private final boolean isSigned;
    private final int maxBitSize;
    private final BigInteger upperBound;
    private final BigInteger lowerBound;
}
