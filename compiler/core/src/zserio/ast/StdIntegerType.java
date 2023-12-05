package zserio.ast;

import java.math.BigInteger;

import zserio.antlr.ZserioParser;

/**
 * AST node for built-in signed and unsigned standard Integer types.
 *
 * Standard Integer types (int8, uint8, ...) are Zserio types as well.
 */
public final class StdIntegerType extends IntegerType implements FixedSizeType
{
    /**
     * Constructor from AST node location, the name and the token type.
     *
     * @param location  AST node location.
     * @param name      Name of the AST node taken from grammar.
     * @param tokenType Grammar token type.
     */
    public StdIntegerType(AstLocation location, String name, int tokenType)
    {
        super(location, name);

        switch (tokenType)
        {
        case ZserioParser.INT8:
            isSigned = true;
            bitSize = 8;
            break;

        case ZserioParser.INT16:
            isSigned = true;
            bitSize = 16;
            break;

        case ZserioParser.INT32:
            isSigned = true;
            bitSize = 32;
            break;

        case ZserioParser.INT64:
            isSigned = true;
            bitSize = 64;
            break;

        case ZserioParser.UINT8:
            isSigned = false;
            bitSize = 8;
            break;

        case ZserioParser.UINT16:
            isSigned = false;
            bitSize = 16;
            break;

        case ZserioParser.UINT32:
            isSigned = false;
            bitSize = 32;
            break;

        case ZserioParser.UINT64:
            isSigned = false;
            bitSize = 64;
            break;

        default:
            throw new InternalError("Unexpected AST node type in StdIntegerType!");
        }

        if (isSigned)
        {
            upperBound = BigInteger.ONE.shiftLeft(bitSize - 1).subtract(BigInteger.ONE);
            lowerBound = upperBound.not();
        }
        else
        {
            upperBound = BigInteger.ONE.shiftLeft(bitSize).subtract(BigInteger.ONE);
            lowerBound = BigInteger.ZERO;
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitStdIntegerType(this);
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

    private final boolean isSigned;
    private final int bitSize;
    private final BigInteger upperBound;
    private final BigInteger lowerBound;
}
