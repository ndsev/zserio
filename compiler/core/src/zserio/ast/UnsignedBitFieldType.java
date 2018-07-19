package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in unsigned bit field integer types.
 *
 * Unsigned bit field integer types (bit:1, bit<expr>, ...) are Zserio types as well.
 */
public class UnsignedBitFieldType extends BitFieldType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitUnsignedBitFieldType(this);
    }

    @Override
    public BigInteger getUpperBound()
    {
        final Integer bitSize = getBitSize();
        if (bitSize == null)
            return null;

        // (1 << length) - 1
        return BigInteger.ONE.shiftLeft(bitSize).subtract(BigInteger.ONE);
    }

    @Override
    public BigInteger getLowerBound()
    {
        return BigInteger.ZERO;
    }

    @Override
    public boolean isSigned()
    {
        return false;
    }

    @Override
    protected int getMaxBitFieldBits()
    {
        return MAX_UNSIGNED_BITFIELD_BITS;
    }

    private static final long serialVersionUID = 2306571678256389121L;
    private static final int MAX_UNSIGNED_BITFIELD_BITS = 63;
}
