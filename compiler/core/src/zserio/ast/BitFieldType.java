package zserio.ast;

import java.math.BigInteger;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;

/**
 * AST abstract node for all bit field Integer types.
 *
 * This is an abstract class for all built-in Zserio bit field Integer types (bit:1, int:1, bit<expr>,
 * int<expr>, ...).
 */
public abstract class BitFieldType extends IntegerType
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token            Token to construct from.
     * @param lengthExpression Length expression associated with this bit field type.
     */
    public BitFieldType(Token token, Expression lengthExpression)
    {
        super(token);

        this.lengthExpression = lengthExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitBitFieldType(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        lengthExpression.accept(visitor);
    }

    /**
     * Gets the expression which gives bit field length.
     *
     * Length expression must be provided according to the grammar, so this method cannot return null.
     *
     * @return Length expression.
     */
    public Expression getLengthExpression()
    {
        return lengthExpression;
    }

    /**
     * Returns the number of bits in this bit field.
     *
     * Number of bits does not have to be known during compile time if and only if the provided length
     * expression is not possible to evaluate during Zserio compilation.
     *
     * @return Number of bits if known at compile time (positive) or null when not known at compile time.
     */
    public Integer getBitSize()
    {
        return bitSize;
    }

    /**
     * Gets the maximal number of bits used by this bit field.
     *
     * If getBitSize() returns non-null, then this is the value returned by getMaxBitSize().
     * If getBitSize() returns null and if length expression provides upper bound, then this is the length
     * expression upper bound.
     * If getBitSize() returns null and if length expression does not provide upper bound, then this is the
     * maximum possible number of bits which can be used by this bit field.
     *
     * @return Maximal number of bits used by this bit field.
     */
    public int getMaxBitSize()
    {
        return maxBitSize;
    }

    /**
     * Evaluates bit sizes of this bit field type.
     *
     * This method can be called from Expression.evaluate() method if some expression refers to bit field type
     * before definition of this type. Therefore 'isEvaluated' check is necessary.
     */
    void evaluate()
    {
        if (!isEvaluated)
        {
            // check length expression
            if (lengthExpression.getExprType() != Expression.ExpressionType.INTEGER)
                throw new ParserException(lengthExpression, "Invalid length expression for bitfield. " +
                        "Length must be integer!");

            // evaluate bit sizes
            final BigInteger lengthValue = lengthExpression.getIntegerValue();
            final int maxBitFieldBits = getMaxBitFieldBits();
            if (lengthValue != null)
            {
                bitSize = lengthValue.intValue();
                if (bitSize < 1 || bitSize > maxBitFieldBits)
                    throw new ParserException(lengthExpression, "Invalid length '" + bitSize +
                            "' for bitfield. Length must be within range [1," + maxBitFieldBits + "]!");
                maxBitSize = bitSize;
            }
            else
            {
                bitSize = null;
                final BigInteger upperBound = lengthExpression.getIntegerUpperBound();
                if (upperBound == null || upperBound.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0)
                    throw new ParserException(lengthExpression, "Invalid length expression for bitfield. " +
                            "Length cannot exceed 32-bits integer type!");

                final int upperBoundIntValue = upperBound.intValue();
                maxBitSize = (upperBoundIntValue > maxBitFieldBits) ? maxBitFieldBits : upperBoundIntValue;
            }

            isEvaluated = true;
        }
    }

    abstract int getMaxBitFieldBits();

    private final Expression lengthExpression;

    private Integer bitSize = null;
    private int maxBitSize = 0;
    private boolean isEvaluated = false;
}
