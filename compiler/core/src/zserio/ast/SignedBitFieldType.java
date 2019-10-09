package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in signed bit field Integer types.
 *
 * Signed bit field Integer types (int:1, int<expr>, ...) are Zserio types as well.
 */
public class SignedBitFieldType extends BitFieldType
{
    /**
     * Constructor from AST node location, the name and the expression length
     *
     * @param location         AST node location.
     * @param name             Name of the AST node taken from grammar.
     * @param lengthExpression Length expression associated with this bit field type.
     */
    public SignedBitFieldType(AstLocation location, String name, Expression lengthExpression)
    {
        super(location, name, lengthExpression);
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

    @Override
    SignedBitFieldType instantiate(Expression instantiatedLengthExpression)
    {
        return new SignedBitFieldType(getLocation(), getName(), instantiatedLengthExpression);
    }

    private static final int MAX_SIGNED_BITFIELD_BITS = 64;
}
