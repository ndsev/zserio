package zserio.ast;

import java.math.BigInteger;

/**
 * AST node for built-in signed and unsigned fixed bit field types.
 *
 * Fixed bit field types (int:8, bit:8, ...) are Zserio built-in bit field types with fixed length.
 */
public final class FixedBitFieldType extends IntegerType implements FixedSizeType
{
    /**
     * Constructor from AST node location, the name and the bit size.
     *
     * @param location    AST node location.
     * @param name        Name of the AST node taken from grammar.
     * @param isSigned    True if the bit field type is signed.
     * @param bitSizeText Bit field type size in bits as a string.
     */
    public FixedBitFieldType(AstLocation location, String name, boolean isSigned, String bitSizeText)
    {
        super(location, name + ":" + bitSizeText);

        int bitSize = 0;
        try
        {
            bitSize = Integer.parseUnsignedInt(bitSizeText);
        }
        catch (NumberFormatException e)
        {
            // bitSize remains 0, exception will be reported
        }

        if (bitSize < 1 || bitSize > MAX_FIXED_BIT_FIELD_BIT_SIZE)
            throw new ParserException(location, "Invalid bit size '" + bitSizeText +
                    "' for the fixed bit field. Length must be within range " +
                    "[1, " + MAX_FIXED_BIT_FIELD_BIT_SIZE + "]!");

        if (isSigned)
        {
            // (1 << (bitSize - 1)) - 1
            upperBound = BigInteger.ONE.shiftLeft(bitSize - 1).subtract(BigInteger.ONE);
            // -(1 << (bitSize - 1))
            lowerBound = BigInteger.ONE.shiftLeft(bitSize - 1).negate();
        }
        else
        {
            // (1 << bitSize) - 1
            upperBound = BigInteger.ONE.shiftLeft(bitSize).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
        }

        this.isSigned = isSigned;
        this.bitSize = bitSize;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitFixedBitFieldType(this);
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

    private static final int MAX_FIXED_BIT_FIELD_BIT_SIZE = 64;

    private final BigInteger upperBound;
    private final BigInteger lowerBound;
    private final boolean isSigned;
    private final int bitSize;
}
