package zserio.ast;

import java.math.BigInteger;

import org.antlr.v4.runtime.Token;

/**
 * AST node for built-in signed bit field Integer types.
 *
 * Signed bit field Integer types (int:1, int<expr>, ...) are Zserio types as well.
 */
public class SignedBitFieldType extends BitFieldType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token            Token to construct from.
     * @param lengthExpression Length expression associated with this signed bit field type.
     */
    public SignedBitFieldType(Token token, Expression lengthExpression)
    {
        super(token, lengthExpression);
    }

    @Override
    public BigInteger getUpperBound()
    {
        final Integer bitSize = getBitSize();
        if (bitSize == null)
            return null;

        // (1 << (bitSize - 1)) - 1
        return BigInteger.ONE.shiftLeft(bitSize - 1).subtract(BigInteger.ONE);
    }

    @Override
    public BigInteger getLowerBound()
    {
        final Integer bitSize = getBitSize();
        if (bitSize == null)
            return null;

        // -(1 << (bitSize - 1))
        return BigInteger.ONE.shiftLeft(bitSize - 1).negate();
    }

    @Override
    public boolean isSigned()
    {
        return true;
    }

    @Override
    int getMaxBitFieldBits()
    {
        return MAX_SIGNED_BITFIELD_BITS;
    }

    private static final int MAX_SIGNED_BITFIELD_BITS = 64;
}
