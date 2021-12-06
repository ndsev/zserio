package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in signed and unsigned dynamic bit field types.
 *
 * Dynamic bit field types (int&lt;expr&gt;, bit&lt;expr&gt;, ...) are Zserio built-in bit field types with
 * length defined by expression.
 */
public class DynamicBitFieldType extends IntegerType
{
    /**
     * Constructor from AST node location, the name and the expression length.
     *
     * @param location         AST node location.
     * @param name             Name of the AST node taken from grammar.
     * @param isSigned         True if the bit field type is signed.
     */
    public DynamicBitFieldType(AstLocation location, String name, boolean isSigned)
    {
        super(location, name);

        this.isSigned = isSigned;
        if (isSigned)
        {
            // (1 << (bitSize - 1)) - 1
            upperBound = BigInteger.ONE.shiftLeft(MAX_DYNAMIC_BIT_FIELD_BIT_SIZE - 1).subtract(BigInteger.ONE);
            // -(1 << (bitSize - 1))
            lowerBound = BigInteger.ONE.shiftLeft(MAX_DYNAMIC_BIT_FIELD_BIT_SIZE - 1).negate();
        }
        else
        {
            // (1 << bitSize) - 1
            upperBound = BigInteger.ONE.shiftLeft(MAX_DYNAMIC_BIT_FIELD_BIT_SIZE).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDynamicBitFieldType(this);
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

    static final int MAX_DYNAMIC_BIT_FIELD_BIT_SIZE = 64;

    private final boolean isSigned;
    private final BigInteger upperBound;
    private final BigInteger lowerBound;
}
