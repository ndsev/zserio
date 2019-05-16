package zserio.emit.common;

import java.math.BigInteger;

import zserio.antlr.ZserioParser;
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

            final int arity = getArity(expr);
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

    private int getArity(Expression expr)
    {
        if (expr.op1() == null)
            return 0;
        if (expr.op2() == null)
            return 1;
        if (expr.op3() == null)
            return 2;
        else
            return 3;
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
            case ZserioParser.DECIMAL_LITERAL:
                buffer.append(policy.getDecimalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.BINARY_LITERAL:
                buffer.append(policy.getBinaryLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.HEXADECIMAL_LITERAL:
                buffer.append(policy.getHexadecimalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.OCTAL_LITERAL:
                buffer.append(policy.getOctalLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.FLOAT_LITERAL:
                buffer.append(policy.getFloatLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.DOUBLE_LITERAL:
                buffer.append(policy.getDoubleLiteral(expr, isNegativeLiteral));
                break;

            case ZserioParser.BOOL_LITERAL:
                buffer.append(policy.getBoolLiteral(expr));
                break;

            case ZserioParser.STRING_LITERAL:
                buffer.append(policy.getStringLiteral(expr));
                break;

            case ZserioParser.INDEX:
                buffer.append(policy.getIndex(expr));
                break;

            case ZserioParser.ID:
                final boolean isLastInDot = !inArray && !inDot;
                final boolean isSetter = isLastInDot && formatSetter;
                buffer.append(policy.getIdentifier(expr, isLastInDot, isSetter));
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
            case ZserioParser.PLUS:
                formatting = policy.getUnaryPlus(expr);
                break;

            case ZserioParser.MINUS:
                wasUnaryMinus = true;
                formatting = policy.getUnaryMinus(expr);
                break;

            case ZserioParser.TILDE:
                formatting = policy.getTilde(expr);
                break;

            case ZserioParser.BANG:
                formatting = policy.getBang(expr);
                break;

            case ZserioParser.LPAREN:
                formatting = policy.getLeftParenthesis(expr);
                break;

            case ZserioParser.RPAREN: // function call
                formatting = policy.getFunctionCall(expr);
                break;

            case ZserioParser.LENGTHOF:
                formatting = policy.getLengthOf(expr);
                break;

            case ZserioParser.SUM:
                formatting = policy.getSum(expr);
                break;

            case ZserioParser.VALUEOF:
                formatting = policy.getValueOf(expr);
                break;

            case ZserioParser.NUMBITS:
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
            case ZserioParser.COMMA:
                formatting = policy.getComma(expr);
                break;

            case ZserioParser.LOGICAL_OR:
                formatting = policy.getLogicalOr(expr);
                break;

            case ZserioParser.LOGICAL_AND:
                formatting = policy.getLogicalAnd(expr);
                break;

            case ZserioParser.OR:
                formatting = policy.getOr(expr);
                break;

            case ZserioParser.XOR:
                formatting = policy.getXor(expr);
                break;

            case ZserioParser.AND:
                formatting = policy.getAnd(expr);
                break;

            case ZserioParser.EQ:
                formatting = policy.getEq(expr);
                break;

            case ZserioParser.NE:
                formatting = policy.getNe(expr);
                break;

            case ZserioParser.LT:
                formatting = policy.getLt(expr);
                break;

            case ZserioParser.LE:
                formatting = policy.getLe(expr);
                break;

            case ZserioParser.GE:
                formatting = policy.getGe(expr);
                break;

            case ZserioParser.GT:
                formatting = policy.getGt(expr);
                break;

            case ZserioParser.LSHIFT:
                formatting = policy.getLeftShift(expr);
                break;

            case ZserioParser.RSHIFT:
                formatting = policy.getRightShift(expr);
                break;

            case ZserioParser.PLUS:
                formatting = policy.getPlus(expr);
                break;

            case ZserioParser.MINUS:
                formatting = policy.getMinus(expr);
                break;

            case ZserioParser.MULTIPLY:
                formatting = policy.getMultiply(expr);
                break;

            case ZserioParser.DIVIDE:
                formatting = policy.getDivide(expr);
                break;

            case ZserioParser.MODULO:
                formatting = policy.getModulo(expr);
                break;

            case ZserioParser.LBRACKET:
                oldInFlag = inArray;
                inArray = true;
                final boolean isSetter = !inDot && formatSetter;
                formatting = policy.getArrayElement(expr, isSetter);
                break;

            case ZserioParser.DOT:
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

        if (expressionType == ZserioParser.LBRACKET)
            inArray = oldInFlag;
        else if (expressionType == ZserioParser.DOT)
            inDot = oldInFlag;

        append(expr.op2());
        buffer.append(formatting.getAfterOperand2());
    }

    private void emitTernaryExpression(Expression expr) throws ZserioEmitException
    {
        ExpressionFormattingPolicy.TernaryExpressionFormatting formatting;
        switch (expr.getType())
        {
            case ZserioParser.QUESTIONMARK:
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
