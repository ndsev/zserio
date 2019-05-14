package zserio.ast;

import java.math.BigInteger;

/**
 * Abstraction for result of integer expression.
 *
 * This abstraction is used during expression evaluation only.
 *
 * The abstraction covers the following:
 * - result (value) of integer expression
 * - lower and upper bounds if result of integer expression value is unknown
 * - flag which is set if expression contains value which needs BigInteger type
 */
class ExpressionIntegerValue
{
    /**
     * Empty constructor.
     */
    public ExpressionIntegerValue()
    {
        value = null;
        lowerBound = null;
        upperBound = null;
        needsBigInteger = false;
    }

    /**
     * Constructor from BigInteger value.
     *
     * @param value Expression value to construct from.
     */
    public ExpressionIntegerValue(BigInteger value)
    {
        this.value = value;
        lowerBound = value;
        upperBound = value;
        needsBigInteger = value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ||
                          value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0;
    }

    /**
     * Constructor from lower and upper bounds.
     *
     * @param lowerBound Lower bound to construct from.
     * @param upperBound Upper bound to construct from.
     */
    public ExpressionIntegerValue(BigInteger lowerBound, BigInteger upperBound)
    {
        value = null;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        needsBigInteger = upperBound.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ||
                lowerBound.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0;
    }

    /**
     * Constructor from BigInteger value, lower and upper bounds and from BigInteger flag.
     *
     * @param lowerBound Lower bound to construct from.
     * @param upperBound Upper bound to construct from.
     */
    public ExpressionIntegerValue(BigInteger value, BigInteger lowerBound, BigInteger upperBound,
            boolean needsBigInteger)
    {
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.needsBigInteger = needsBigInteger;
    }

    /**
     * Gets integer expression value.
     *
     * @return Integer expression value or null if integer expression value is unknown.
     */
    public BigInteger getValue()
    {
        return value;
    }

    /**
     * Gets lowe bound of integer expression value.
     *
     * @return Lower bound of integer expression value or null if lower bound is unknown or if exact integer
     *         expression value is available.
     */
    public BigInteger getLowerBound()
    {
        return lowerBound;
    }

    /**
     * Gets upper bound of integer expression value.
     *
     * @return Upper bound of integer expression value or null if upper bound is unknown or if exact integer
     *         expression value is available.
     */
    public BigInteger getUpperBound()
    {
        return upperBound;
    }

    /**
     * Gets BigInteger flag.
     *
     * @return Returns true if expression contains value which needs BigInteger type.
     */
    public boolean needsBigInteger()
    {
        return needsBigInteger;
    }

    /**
     * Negates integer expression value.
     *
     * @return Returns new integer expression value with result of negation.
     */
    public ExpressionIntegerValue negate()
    {
        if (value != null)
            return new ExpressionIntegerValue(value.negate());

        if (lowerBound != null && upperBound != null)
            return new ExpressionIntegerValue(lowerBound.negate(), upperBound.negate(), needsBigInteger);

        return new ExpressionIntegerValue(needsBigInteger);
    }

    /**
     * Adds another integer expression value.
     *
     * @param operand Integer expression value to add.
     *
     * @return Returns new integer expression value with result of addition.
     */
    public ExpressionIntegerValue add(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.add(operand.value));

        final boolean newNeedsBigInteger = needsBigInteger || operand.needsBigInteger;
        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            return new ExpressionIntegerValue(lowerBound.add(operand.lowerBound),
                    upperBound.add(operand.upperBound), newNeedsBigInteger);
        }

        return new ExpressionIntegerValue(newNeedsBigInteger);
    }

    /**
     * Subtracts another integer expression value.
     *
     * @param operand Integer expression value to subtract.
     *
     * @return Returns new integer expression value with result of subtraction.
     */
    public ExpressionIntegerValue subtract(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.subtract(operand.value));

        final boolean newNeedsBigInteger = needsBigInteger || operand.needsBigInteger;
        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            return new ExpressionIntegerValue(lowerBound.subtract(operand.upperBound),
                    upperBound.subtract(operand.lowerBound), newNeedsBigInteger);
        }

        return new ExpressionIntegerValue(newNeedsBigInteger);
    }

    /**
     * Multiplies another integer expression value.
     *
     * @param operand Integer expression value to multiply.
     *
     * @return Returns new integer expression value with result of multiplication.
     */
    public ExpressionIntegerValue multiply(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.multiply(operand.value));

        final boolean newNeedsBigInteger = needsBigInteger || operand.needsBigInteger;
        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            final MinMaxValue minMaxValue = new MinMaxValue();
            minMaxValue.add(lowerBound.multiply(operand.lowerBound));
            minMaxValue.add(lowerBound.multiply(operand.upperBound));
            minMaxValue.add(upperBound.multiply(operand.lowerBound));
            minMaxValue.add(upperBound.multiply(operand.upperBound));

            return new ExpressionIntegerValue(minMaxValue.getMin(), minMaxValue.getMax(), newNeedsBigInteger);
        }

        return new ExpressionIntegerValue(newNeedsBigInteger);
    }

    /**
     * Divides another integer expression value.
     *
     * @param operand Integer expression value to divide.
     *
     * @return Returns new integer expression value with result of division.
     */
    public ExpressionIntegerValue divide(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.divide(operand.value));

        final boolean newNeedsBigInteger = needsBigInteger || operand.needsBigInteger;
        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            if (operand.lowerBound.compareTo(BigInteger.ZERO) != 0 ||
                operand.upperBound.compareTo(BigInteger.ZERO) != 0)
            {
                // this is not division by degenerated interval [0,0]
                final BigInteger operandLowerBound = (operand.lowerBound.compareTo(BigInteger.ZERO) == 0) ?
                        BigInteger.ONE : operand.lowerBound;
                final BigInteger operandUpperBound = (operand.upperBound.compareTo(BigInteger.ZERO) == 0) ?
                        BigInteger.ONE.negate() : operand.upperBound;
                final MinMaxValue minMaxValue = new MinMaxValue();

                minMaxValue.add(lowerBound.divide(operandLowerBound));
                minMaxValue.add(lowerBound.divide(operandUpperBound));
                minMaxValue.add(upperBound.divide(operandLowerBound));
                minMaxValue.add(upperBound.divide(operandUpperBound));

                if (operandLowerBound.compareTo(BigInteger.ZERO) <= 0 &&
                        operandUpperBound.compareTo(BigInteger.ZERO) >= 0)
                {
                    // include -1 and +1 in the minmax calculation
                    minMaxValue.add(lowerBound);            // lowerBound.divide(+1)
                    minMaxValue.add(upperBound);            // upperBound.divide(+1)
                    minMaxValue.add(lowerBound.negate());   // lowerBound.divide(-1)
                    minMaxValue.add(upperBound.negate());   // upperBound.divide(-1)
                }

                return new ExpressionIntegerValue(minMaxValue.getMin(), minMaxValue.getMax(),
                        newNeedsBigInteger);
            }
        }

        return new ExpressionIntegerValue(newNeedsBigInteger);
    }

    /**
     * Calculates module using another integer expression value.
     *
     * @param operand Integer expression value to use for module calculation.
     *
     * @return Returns new integer expression value with result of module operation.
     */
    public ExpressionIntegerValue remainder(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.remainder(operand.value));

        return new ExpressionIntegerValue(needsBigInteger || operand.needsBigInteger);
    }

    /**
     * Shifts left using another integer expression value.
     *
     * @param operand Integer expression value to use for left shift operation.
     *
     * @return Returns new integer expression value with result of left shift operation.
     */
    public ExpressionIntegerValue shiftLeft(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.shiftLeft(operand.value.intValue()));

        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            return new ExpressionIntegerValue(lowerBound.shiftLeft(operand.lowerBound.intValue()),
                    upperBound.shiftLeft(operand.upperBound.intValue()), needsBigInteger);
        }

        return new ExpressionIntegerValue(needsBigInteger);
    }

    /**
     * Shifts right using another integer expression value.
     *
     * @param operand Integer expression value to use for right shift operation.
     *
     * @return Returns new integer expression value with result of right shift operation.
     */
    public ExpressionIntegerValue shiftRight(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.shiftRight(operand.value.intValue()));

        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
        {
            return new ExpressionIntegerValue(lowerBound.shiftRight(operand.lowerBound.intValue()),
                    upperBound.shiftRight(operand.upperBound.intValue()), needsBigInteger);
        }

        return new ExpressionIntegerValue(needsBigInteger);
    }

    /**
     * Bitwise 'and' using another integer expression value.
     *
     * @param operand Integer expression value to use for bitwise 'and' operation.
     *
     * @return Returns new integer expression value with result of bitwise 'and' operation.
     */
    public ExpressionIntegerValue and(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.and(operand.value));

        return new ExpressionIntegerValue(needsBigInteger || operand.needsBigInteger);
    }

    /**
     * Bitwise 'or' using another integer expression value.
     *
     * @param operand Integer expression value to use for bitwise 'or' operation.
     *
     * @return Returns new integer expression value with result of bitwise 'or' operation.
     */
    public ExpressionIntegerValue or(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.or(operand.value));

        return new ExpressionIntegerValue(needsBigInteger || operand.needsBigInteger);
    }

    /**
     * Bitwise 'xor' using another integer expression value.
     *
     * @param operand Integer expression value to use for bitwise 'xor' operation.
     *
     * @return Returns new integer expression value with result of bitwise 'xor' operation.
     */
    public ExpressionIntegerValue xor(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.xor(operand.value));

        return new ExpressionIntegerValue(needsBigInteger || operand.needsBigInteger);
    }

    /**
     * Bitwise 'not' operator.
     *
     * @return Returns new integer expression value with result of bitwise 'not' operation.
     */
    public ExpressionIntegerValue not()
    {
        if (value != null)
            return new ExpressionIntegerValue(value.not());

        return new ExpressionIntegerValue(needsBigInteger);
    }

    /**
     * 'numbits' operator.
     *
     * @return Returns new integer expression value with result of 'numbits' operation.
     */
    public ExpressionIntegerValue numbits()
    {
        if (value != null)
            return new ExpressionIntegerValue(getNumBits(value));

        if (lowerBound != null && upperBound != null)
            return new ExpressionIntegerValue(getNumBits(lowerBound), getNumBits(upperBound), false);

        return new ExpressionIntegerValue(false);
    }

    /**
     * Conditional operator.
     *
     * @param operand Integer expression value to use for conditional operator.
     *
     * @return Returns new integer expression value with result of conditional operator.
     */
    public ExpressionIntegerValue conditional(ExpressionIntegerValue operand)
    {
        if (value != null && operand.value != null)
            return new ExpressionIntegerValue(value.min(operand.value), value.max(operand.value));

        final boolean newNeedsBigInteger = needsBigInteger || operand.needsBigInteger;
        if (lowerBound != null && upperBound != null && operand.lowerBound != null &&
                operand.upperBound != null)
            return new ExpressionIntegerValue(lowerBound.min(operand.lowerBound),
                    upperBound.max(operand.upperBound), newNeedsBigInteger);

        return new ExpressionIntegerValue(newNeedsBigInteger);
    }

    /**
     * Any relational operator.
     *
     * Result of relational operator is not integer any more. However uint64 type flag must be updated.
     *
     * @param operand Integer expression value to use for relational operator.
     *
     * @return Returns new integer expression value with result of relational operator.
     */
    public ExpressionIntegerValue relationalOperator(ExpressionIntegerValue operand)
    {
        return new ExpressionIntegerValue(needsBigInteger || operand.needsBigInteger);
    }

    /**
     * Private constructor from lower and upper bounds and from BigInteger flag.
     *
     * @param lowerBound      Lower bound to construct from.
     * @param upperBound      Upper bound to construct from.
     * @param needsBigInteger BigInteger flag to construct from.
     */
    private ExpressionIntegerValue(BigInteger lowerBound, BigInteger upperBound, boolean needsBigInteger)
    {
        value = null;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.needsBigInteger = needsBigInteger;
    }

    /**
     * Private constructor from BigInteger flag.
     *
     * @param needsBigInteger BigInteger flag to construct from.
     */
    private ExpressionIntegerValue(boolean needsBigInteger)
    {
        value = null;
        lowerBound = null;
        upperBound = null;
        this.needsBigInteger = needsBigInteger;
    }

    // TODO This is redundant with runtime library but we don't have available runtime library in Zserio core.
    private BigInteger getNumBits(BigInteger value)
    {
        if (value.doubleValue() < 2.0)
            return BigInteger.ONE;

        final BigInteger calcValue = value.subtract(BigInteger.ONE);
        return BigInteger.valueOf(calcValue.bitLength());
    }

    private static class MinMaxValue
    {
        public void add(BigInteger value)
        {
            if (minValue == null || minValue.compareTo(value) > 0)
            {
                minValue = value;
            }

            if (maxValue == null || maxValue.compareTo(value) < 0)
            {
                maxValue = value;
            }
        }

        public BigInteger getMin()
        {
            return minValue;
        }

        public BigInteger getMax()
        {
            return maxValue;
        }

        private BigInteger minValue;
        private BigInteger maxValue;
    }

    private final BigInteger value;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;
    private final boolean needsBigInteger;
}
