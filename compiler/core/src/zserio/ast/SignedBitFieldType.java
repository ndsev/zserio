package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in signed bit field integer types.
 *
 * Signed bit field integer types (int:1, int<expr>, ...) are Zserio types as well.
 */
public class SignedBitFieldType extends BitFieldType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitSignedBitFieldType(this);
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
    protected int getMaxBitFieldBits()
    {
        return MAX_SIGNED_BITFIELD_BITS;
    }

    private static final long serialVersionUID = 2306571678256389119L;
    private static final int MAX_SIGNED_BITFIELD_BITS = 64;
}
