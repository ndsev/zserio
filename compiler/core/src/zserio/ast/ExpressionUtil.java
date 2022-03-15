package zserio.ast;

import java.math.BigInteger;
import java.util.Locale;

/**
 * This class implements various utilities on Expression type.
 */
class ExpressionUtil
{
    /**
     * Checks expression type according given type instantiation.
     *
     * @param expression        Expression to use for checking.
     * @param typeInstantiation Type instantiation to use for checking.
     *
     * @throws ParserException Throws if expression type does not correspond to the type instantiation.
     */
    static void checkExpressionType(Expression expression, TypeInstantiation typeInstantiation)
    {
        checkExpressionType(expression, typeInstantiation.getBaseType(), typeInstantiation);
    }

    /**
     * Checks expression type according given type reference.
     * Should be used only when no type instantiation is available (e.g. in parameters).
     *
     * @param expression    Expression to use for checking.
     * @param typeReference Type reference to use for checking.
     *
     * @throws ParserException Throws if expression type does not correspond to the type reference.
     */
    static void checkExpressionType(Expression expression, TypeReference typeReference)
    {
        checkExpressionType(expression, typeReference.getBaseTypeReference().getType(), null);
    }

    /**
     * Checks if integer expression is within the range of Zserio type.
     *
     * @param expression    Expression to use for checking.
     * @param instantiation Type intantiation to use for checking.
     * @param ownerName     Name of Zserio type which owns the given expression.
     *
     * @throws ParserException Throws if integer expression exceeds the bounds of its type.
     */
    static void checkIntegerExpressionRange(Expression expression, TypeInstantiation instantiation,
            String ownerName)
    {
        final ZserioType type = instantiation.getBaseType();
        if (type instanceof IntegerType)
        {
            final IntegerType integerType = (IntegerType)type;
            final BigInteger value = expression.getIntegerValue();
            if (value != null)
            {
                if (value.compareTo(integerType.getLowerBound(instantiation)) < 0 ||
                    value.compareTo(integerType.getUpperBound(instantiation)) > 0)
                {
                    throw new ParserException(expression, "Initializer value '" + value.toString() + "' of '" +
                            ownerName + "' exceeds the bounds of its type '" + type.getName() + "'!");
                }
            }
        }
    }

    private static void checkExpressionType(Expression expression, ZserioType type,
            TypeInstantiation instantiation)
    {
        boolean isTypeMismatch = true;
        if (type instanceof IntegerType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.INTEGER);
            if (!isTypeMismatch)
            {
                final BigInteger upperBound = ((IntegerType)type).getUpperBound(instantiation);
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
            if (!isTypeMismatch)
                checkUserTypes(expression, type);
        }
        else if (type instanceof BitmaskType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.BITMASK);
            if (!isTypeMismatch)
                checkUserTypes(expression, type);
        }
        else if (type instanceof CompoundType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.COMPOUND);
            if (!isTypeMismatch)
                checkUserTypes(expression, type);
        }
        else if (type instanceof ExternType)
        {
            isTypeMismatch = (expression.getExprType() != Expression.ExpressionType.EXTERN);
        }

        if (isTypeMismatch)
        {
            final String typeName = (type instanceof ArrayType) ? "array type" : type.getName();
            throw new ParserException(expression, "Wrong type of value expression (" +
                    expression.getExprType().toString().toLowerCase(Locale.ENGLISH) +
                    " cannot be assigned to " + typeName + ")!");
        }
    }

    private static void checkUserTypes(Expression expression, ZserioType type)
    {
        final ZserioType zserioType = expression.getExprZserioType();
        if (zserioType != type)
        {
            throw new ParserException(expression, "Wrong type of value expression ('" +
                    zserioType.getName() + "' cannot be assigned to '" + type.getName() + "')!");
        }
    }
}
