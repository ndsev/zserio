package zserio.ast;

import java.math.BigInteger;
import java.util.Locale;

import zserio.antlr.util.ParserException;

/**
 * This class implements various utilities on Expression type.
 */
class ExpressionUtil
{
    /**
     * Checks expression type according given Zserio type.
     *
     * @param expression Expression to use for checking.
     * @param type       Zserio type to use for checking.
     *
     * @throws ParserException Throws if expression type does not correspond to Zserio type.
     */
    public static void checkExpressionType(Expression expression, ZserioType type)
    {
        boolean isTypeMismatch = true;
        if (type instanceof IntegerType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.INTEGER);
            if (!isTypeMismatch)
            {
                final BigInteger upperBound = ((IntegerType)type).getUpperBound();
                if (upperBound.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)
                {
                    // Zserio type value will need BigInteger for mapping => propagate it into expression
                    expression.propagateNeedsBigInteger();
                }
                else if (expression.needsBigInteger())
                {
                    // Expression needs BigInteger but Zserio type is native => cast expression to long
                    expression.setNeedsBigIntegerCastingNative();
                }
            }
        }
        else if (type instanceof FloatType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.FLOAT &&
                              expression.getExprType() != Expression.ExpressionType.INTEGER);
        }
        else if (type instanceof StringType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.STRING);
        }
        else if (type instanceof BooleanType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.BOOLEAN);
        }
        else if (type instanceof EnumType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.ENUM);
        }
        else if (type instanceof CompoundType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.COMPOUND);
        }

        if (isTypeMismatch)
            throw new ParserException(expression, "Wrong type of value expression (" +
                    expression.getExprType().toString().toLowerCase(Locale.ENGLISH) +
                    " cannot be assigned to " + type.getName() + ")!");
    }

    /**
     * Checks if integer expression is within the range of Zserio type.
     *
     * @param expression Expression to use for checking.
     * @param type       Zserio type to use for checking.
     * @param ownerName  Name of Zserio type which owns the given expression.
     *
     * @throws ParserException Throws if integer expression exceeds the bounds of its type.
     */
    public static void checkIntegerExpressionRange(Expression expression, ZserioType type, String ownerName)
    {
        if (type instanceof IntegerType)
        {
            final IntegerType integerType = (IntegerType)type;
            final BigInteger value = expression.getIntegerValue();
            if (value != null)
            {
                if (value.compareTo(integerType.getLowerBound()) < 0 ||
                    value.compareTo(integerType.getUpperBound()) > 0)
                    throw new ParserException(expression, "Initializer value '" + value.toString() + "' of '" +
                            ownerName + "' exceeds the bounds of its type '" + type.getName() + "'!");
            }
        }
    }
}
