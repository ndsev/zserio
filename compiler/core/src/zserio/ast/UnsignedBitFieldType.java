package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in unsigned bit field Integer types.
 *
 * Unsigned bit field Integer types (bit:1, bit<expr>, ...) are Zserio types as well.
 */
public class UnsignedBitFieldType extends BitFieldType
{
    /**
     * Constructor from AST node location, the name and the expression length
     *
     * @param location         AST node location.
     * @param name             Name of the AST node taken from grammar.
     * @param lengthExpression Length expression associated with this bit field type.
     */
    public UnsignedBitFieldType(AstLocation location, String name, Expression lengthExpression)
    {
        super(location, name, lengthExpression);
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
    int getMaxBitFieldBits()
    {
        return MAX_UNSIGNED_BITFIELD_BITS;
    }

    @Override
    UnsignedBitFieldType instantiate(Expression instantiatedLengthExpression)
    {
        return new UnsignedBitFieldType(getLocation(), getName(), instantiatedLengthExpression);
    }

    private static final int MAX_UNSIGNED_BITFIELD_BITS = 63;
}
