package zserio.ast4;

import java.math.BigInteger;

import org.antlr.v4.runtime.Token;

/**
 * AST node for built-in unsigned bit field integer types.
 *
 * Unsigned bit field integer types (bit:1, bit<expr>, ...) are Zserio types as well.
 */
public class UnsignedBitFieldType extends BitFieldType
{
    public UnsignedBitFieldType(Token token)
    {
        super(token);
    }

    /*@Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitUnsignedBitFieldType(this);
    }*/

    @Override
    public void walk(ZserioListener listener)
    {
        listener.enterUnsignedBitFieldType(this);
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

    private static final int MAX_UNSIGNED_BITFIELD_BITS = 63;
}
