package zserio.ast;

import java.math.BigInteger;

import zserio.antlr.ZserioParser;

/**
 * AST node for built-in signed and unsigned variable Integer types.
 *
 * Variable Integer types (varint16, varuint16, ...) are Zserio types as well.
 */
public class VarIntegerType extends IntegerType
{
    /**
     * Constructor from AST node location, the name and the token type.
     *
     * @param location  AST node location.
     * @param name      Name of the AST node taken from grammar.
     * @param tokenType Grammar token type.
     */
    public VarIntegerType(AstLocation location, String name, int tokenType)
    {
        super(location, name);

        switch (tokenType)
        {
        case ZserioParser.VARUINT16:
            isSigned = false;
            maxBitSize = 16;
            upperBound = VARUINT16_UPPER_BOUND;
            lowerBound = VARUINT16_LOWER_BOUND;
            break;

        case ZserioParser.VARINT16:
            isSigned = true;
            maxBitSize = 16;
            upperBound = VARINT16_UPPER_BOUND;
            lowerBound = VARINT16_LOWER_BOUND;
            break;

        case ZserioParser.VARUINT32:
            isSigned = false;
            maxBitSize = 32;
            upperBound = VARUINT32_UPPER_BOUND;
            lowerBound = VARUINT32_LOWER_BOUND;
            break;

        case ZserioParser.VARINT32:
            isSigned = true;
            maxBitSize = 32;
            upperBound = VARINT32_UPPER_BOUND;
            lowerBound = VARINT32_LOWER_BOUND;
            break;

        case ZserioParser.VARUINT64:
            isSigned = false;
            maxBitSize = 64;
            upperBound = VARUINT64_UPPER_BOUND;
            lowerBound = VARUINT64_LOWER_BOUND;
            break;

        case ZserioParser.VARINT64:
            isSigned = true;
            maxBitSize = 64;
            upperBound = VARINT64_UPPER_BOUND;
            lowerBound = VARINT64_LOWER_BOUND;
            break;

        case ZserioParser.VARUINT:
            isSigned = false;
            maxBitSize = 72;
            upperBound = VARUINT_UPPER_BOUND;
            lowerBound = VARUINT_LOWER_BOUND;
            break;

        case ZserioParser.VARINT:
            isSigned = true;
            maxBitSize = 72;
            upperBound = VARINT_UPPER_BOUND;
            lowerBound = VARINT_LOWER_BOUND;
            break;

        case ZserioParser.VARSIZE:
            isSigned = false;
            maxBitSize = 40;
            upperBound = VARSIZE_UPPER_BOUND;
            lowerBound = VARSIZE_LOWER_BOUND;
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

    public static final BigInteger VARUINT16_UPPER_BOUND =
            BigInteger.ONE.shiftLeft(15).subtract(BigInteger.ONE);
    public static final BigInteger VARUINT16_LOWER_BOUND = BigInteger.ZERO;

    public static final BigInteger VARINT16_UPPER_BOUND = BigInteger.ONE.shiftLeft(14).subtract(BigInteger.ONE);
    public static final BigInteger VARINT16_LOWER_BOUND = VARINT16_UPPER_BOUND.negate();

    public static final BigInteger VARUINT32_UPPER_BOUND =
            BigInteger.ONE.shiftLeft(29).subtract(BigInteger.ONE);
    public static final BigInteger VARUINT32_LOWER_BOUND = BigInteger.ZERO;

    public static final BigInteger VARINT32_UPPER_BOUND = BigInteger.ONE.shiftLeft(28).subtract(BigInteger.ONE);
    public static final BigInteger VARINT32_LOWER_BOUND = VARINT32_UPPER_BOUND.negate();

    public static final BigInteger VARUINT64_UPPER_BOUND =
            BigInteger.ONE.shiftLeft(57).subtract(BigInteger.ONE);
    public static final BigInteger VARUINT64_LOWER_BOUND = BigInteger.ZERO;

    public static final BigInteger VARINT64_UPPER_BOUND = BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE);
    public static final BigInteger VARINT64_LOWER_BOUND = VARINT64_UPPER_BOUND.negate();

    public static final BigInteger VARUINT_UPPER_BOUND = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
    public static final BigInteger VARUINT_LOWER_BOUND = BigInteger.ZERO;

    public static final BigInteger VARINT_UPPER_BOUND = BigInteger.valueOf(Long.MAX_VALUE);
    public static final BigInteger VARINT_LOWER_BOUND = BigInteger.valueOf(Long.MIN_VALUE);

    public static final BigInteger VARSIZE_UPPER_BOUND = BigInteger.ONE.shiftLeft(31).subtract(BigInteger.ONE);
    public static final BigInteger VARSIZE_LOWER_BOUND = BigInteger.ZERO;

    private final boolean isSigned;
    private final int maxBitSize;
    private final BigInteger upperBound;
    private final BigInteger lowerBound;
}
