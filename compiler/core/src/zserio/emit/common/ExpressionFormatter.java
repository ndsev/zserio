package zserio.emit.common;

import java.math.BigInteger;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.ast.Expression;

/**
 * Expression formatter used by all emitters.
 */
public class ExpressionFormatter
{
    /**
     * Constructor from expression formatting policy.
     *
     * @param policy Expression formatting policy to construct from.
     */
    public ExpressionFormatter(ExpressionFormattingPolicy policy)
    {
        this.policy = policy;
    }

    /**
     * Formats expression into string for setter accessors.
     *
     * @param expr Expression to format.
     *
     * @return Formatted expression in string format.
     *
     * @throws ZserioEmitException Throws if expression has unexpected format.
     */
    public String formatSetter(Expression expr) throws ZserioEmitException
    {
        return format(expr, true, false);
    }

    /**
     * Formats expression into string for getter accessors.
     *
     * @param expr Expression to format.
     *
     * @return Formatted expression in string format.
     *
     * @throws ZserioEmitException Throws if expression has unexpected format.
     */
    public String formatGetter(Expression expr) throws ZserioEmitException
    {
        return format(expr, false, false);
    }

    /**
     * Formats expression into string for getter accessors together with evaluation of integer subexpressions.
     *
     * All integer subexpressions will be evaluated to integer numbers.
     *
     * @param expr Expression to format.
     *
     * @return Formatted expression in string format.
     *
     * @throws ZserioEmitException Throws if expression has unexpected format.
     */
    public String formatGetterWithEvaluatedIntegers(Expression expr) throws ZserioEmitException
    {
        return format(expr, false, true);
    }

    private String format(Expression expr, boolean formatSetter, boolean evaluateIntegers)
            throws ZserioEmitException
    {
        buffer = new StringBuilder();
        this.formatSetter = formatSetter;
        this.evaluateIntegers = evaluateIntegers;
        wasUnaryMinus = false;
        inArray = false;
        inDot = false;

        final ExpressionFormattingPolicy.UnaryExpressionFormatting castingFormatting =
                policy.getBigIntegerCastingToNative(expr);
        final boolean needsBigIntegerCastingToNative = expr.needsBigIntegerCastingToNative();
        if (needsBigIntegerCastingToNative)
            buffer.append(castingFormatting.getBeforeOperand());

        append(expr);

        if (needsBigIntegerCastingToNative)
            buffer.append(castingFormatting.getAfterOperand());

        return buffer.toString();
    }

    private void append(Expression expr) throws ZserioEmitException
    {
        if (!tryEmitExpressionAsIntegerValue(expr))
        {
            final boolean isNegativeLiteral = wasUnaryMinus;
            wasUnaryMinus = false;

            final int arity = expr.getNumberOfChildren();
            switch (arity)
            {
                case 0:
                    emitAtom(expr, isNegativeLiteral);
                    break;

                case 1:
                    emitUnaryExpression(expr);
                    break;

                case 2:
                    emitBinaryExpression(expr);
                    break;

                case 3:
                    emitTernaryExpression(expr);
                    break;

                default:
                    throw new ZserioEmitException("Expression with unexpected arity (" + arity +
                            ") encountered!");
            }
        }
    }

    private boolean tryEmitExpressionAsIntegerValue(Expression expr)
    {
        if (!evaluateIntegers)
            return false;

        // try to resolve expression to a integer value
        final BigInteger value = expr.getIntegerValue();
        if (value == null)
            return false;

        buffer.append(value.toString());

        return true;
    }

    private void emitAtom(Expression expr, boolean isNegativeLiteral) throws ZserioEmitException
    {
        switch (expr.getType())
        {
            case ZserioParserTokenTypes.DECIMAL_LITERAL:
                buffer.append(policy.getDecimalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.BINARY_LITERAL:
                buffer.append(policy.getBinaryLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.HEXADECIMAL_LITERAL:
                buffer.append(policy.getHexadecimalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.OCTAL_LITERAL:
                buffer.append(policy.getOctalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.FLOAT_LITERAL:
                buffer.append(policy.getFloatLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.DOUBLE_LITERAL:
                buffer.append(policy.getDoubleLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParserTokenTypes.BOOL_LITERAL:
                buffer.append(policy.getBoolLiteral(expr));
                break;

            case ZserioParserTokenTypes.STRING_LITERAL:
                buffer.append(policy.getStringLiteral(expr));
                break;

            case ZserioParserTokenTypes.INDEX:
                buffer.append(policy.getIndex(expr));
                break;

            case ZserioParserTokenTypes.ID:
                final boolean isLast = !inArray && !inDot;
                final boolean isSetter = isLast && formatSetter;
                buffer.append(policy.getIdentifier(expr, isLast, isSetter));
                break;

            default:
                throw new ZserioEmitException("Unknown atom expression type " + expr.getType() + "!");
        }
    }

    private void emitUnaryExpression(Expression expr) throws ZserioEmitException
    {
        ExpressionFormattingPolicy.UnaryExpressionFormatting formatting;
        switch (expr.getType())
        {
            case ZserioParserTokenTypes.UPLUS:
                formatting = policy.getUnaryPlus(expr);
                break;

            case ZserioParserTokenTypes.UMINUS:
                wasUnaryMinus = true;
                formatting = policy.getUnaryMinus(expr);
                break;

            case ZserioParserTokenTypes.TILDE:
                formatting = policy.getTilde(expr);
                break;

            case ZserioParserTokenTypes.BANG:
                formatting = policy.getBang(expr);
                break;

            case ZserioParserTokenTypes.LPAREN:
                formatting = policy.getLeftParenthesis(expr);
                break;

            case ZserioParserTokenTypes.FUNCTIONCALL:
                formatting = policy.getFunctionCall(expr);
                break;

            case ZserioParserTokenTypes.LENGTHOF:
                formatting = policy.getLengthOf(expr);
                break;

            case ZserioParserTokenTypes.SUM:
                formatting = policy.getSum(expr);
                break;

            case ZserioParserTokenTypes.EXPLICIT:
                formatting = policy.getExplicit(expr);
                break;

            case ZserioParserTokenTypes.NUMBITS:
                formatting = policy.getNumBits(expr);
                break;

            default:
                throw new ZserioEmitException("Unknown unary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand());
        append(expr.op1());
        buffer.append(formatting.getAfterOperand());
    }

    private void emitBinaryExpression(Expression expr) throws ZserioEmitException
    {
        ExpressionFormattingPolicy.BinaryExpressionFormatting formatting;
        boolean oldInFlag = false;
        final int expressionType = expr.getType();
        switch (expressionType)
        {
            case ZserioParserTokenTypes.COMMA:
                formatting = policy.getComma(expr);
                break;

            case ZserioParserTokenTypes.LOGICALOR:
                formatting = policy.getLogicalOr(expr);
                break;

            case ZserioParserTokenTypes.LOGICALAND:
                formatting = policy.getLogicalAnd(expr);
                break;

            case ZserioParserTokenTypes.OR:
                formatting = policy.getOr(expr);
                break;

            case ZserioParserTokenTypes.XOR:
                formatting = policy.getXor(expr);
                break;

            case ZserioParserTokenTypes.AND:
                formatting = policy.getAnd(expr);
                break;

            case ZserioParserTokenTypes.EQ:
                formatting = policy.getEq(expr);
                break;

            case ZserioParserTokenTypes.NE:
                formatting = policy.getNe(expr);
                break;

            case ZserioParserTokenTypes.LT:
                formatting = policy.getLt(expr);
                break;

            case ZserioParserTokenTypes.LE:
                formatting = policy.getLe(expr);
                break;

            case ZserioParserTokenTypes.GE:
                formatting = policy.getGe(expr);
                break;

            case ZserioParserTokenTypes.GT:
                formatting = policy.getGt(expr);
                break;

            case ZserioParserTokenTypes.LSHIFT:
                formatting = policy.getLeftShift(expr);
                break;

            case ZserioParserTokenTypes.RSHIFT:
                formatting = policy.getRightShift(expr);
                break;

            case ZserioParserTokenTypes.PLUS:
                formatting = policy.getPlus(expr);
                break;

            case ZserioParserTokenTypes.MINUS:
                formatting = policy.getMinus(expr);
                break;

            case ZserioParserTokenTypes.MULTIPLY:
                formatting = policy.getMultiply(expr);
                break;

            case ZserioParserTokenTypes.DIVIDE:
                formatting = policy.getDivide(expr);
                break;

            case ZserioParserTokenTypes.MODULO:
                formatting = policy.getModulo(expr);
                break;

            case ZserioParserTokenTypes.ARRAYELEM:
                oldInFlag = inArray;
                inArray = true;
                final boolean isSetter = !inDot && formatSetter;
                formatting = policy.getArrayElement(expr, isSetter);
                break;

            case ZserioParserTokenTypes.DOT:
                oldInFlag = inDot;
                inDot = true;
                formatting = policy.getDot(expr);
                break;

            default:
                throw new ZserioEmitException("Unknown binary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand1());
        append(expr.op1());
        buffer.append(formatting.getAfterOperand1());

        if (expressionType == ZserioParserTokenTypes.ARRAYELEM)
            inArray = oldInFlag;
        else if (expressionType == ZserioParserTokenTypes.DOT)
            inDot = oldInFlag;

        append(expr.op2());
        buffer.append(formatting.getAfterOperand2());
    }

    private void emitTernaryExpression(Expression expr) throws ZserioEmitException
    {
        ExpressionFormattingPolicy.TernaryExpressionFormatting formatting;
        switch (expr.getType())
        {
            case ZserioParserTokenTypes.QUESTIONMARK:
                formatting = policy.getQuestionMark(expr);
                break;

            default:
                throw new ZserioEmitException("Unknown ternary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand1());
        append(formatting.getOperand1());
        buffer.append(formatting.getAfterOperand1());
        append(formatting.getOperand2());
        buffer.append(formatting.getAfterOperand2());
        append(formatting.getOperand3());
        buffer.append(formatting.getAfterOperand3());
    }

    private final ExpressionFormattingPolicy policy;

    private StringBuilder buffer;
    private boolean formatSetter;
    private boolean evaluateIntegers;
    private boolean wasUnaryMinus;
    private boolean inDot;
    private boolean inArray;
}
