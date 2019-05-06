package zserio.emit.common;

import java.math.BigInteger;

import zserio.antlr.Zserio4Parser;
import zserio.ast4.Expression;

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
            case Zserio4Parser.DECIMAL_LITERAL:
                buffer.append(policy.getDecimalLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.BINARY_LITERAL:
                buffer.append(policy.getBinaryLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.HEXADECIMAL_LITERAL:
                buffer.append(policy.getHexadecimalLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.OCTAL_LITERAL:
                buffer.append(policy.getOctalLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.FLOAT_LITERAL:
                buffer.append(policy.getFloatLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.DOUBLE_LITERAL:
                buffer.append(policy.getDoubleLiteral(expr, isNegativeLiteral));
                break;

            case Zserio4Parser.BOOL_LITERAL:
                buffer.append(policy.getBoolLiteral(expr));
                break;

            case Zserio4Parser.STRING_LITERAL:
                buffer.append(policy.getStringLiteral(expr));
                break;

            case Zserio4Parser.INDEX:
                buffer.append(policy.getIndex(expr));
                break;

            case Zserio4Parser.ID:
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
            case Zserio4Parser.PLUS:
                formatting = policy.getUnaryPlus(expr);
                break;

            case Zserio4Parser.MINUS:
                wasUnaryMinus = true;
                formatting = policy.getUnaryMinus(expr);
                break;

            case Zserio4Parser.TILDE:
                formatting = policy.getTilde(expr);
                break;

            case Zserio4Parser.BANG:
                formatting = policy.getBang(expr);
                break;

            case Zserio4Parser.LPAREN:
                formatting = policy.getLeftParenthesis(expr);
                break;

            case Zserio4Parser.RPAREN: // function call
                formatting = policy.getFunctionCall(expr);
                break;

            case Zserio4Parser.LENGTHOF:
                formatting = policy.getLengthOf(expr);
                break;

            case Zserio4Parser.SUM:
                formatting = policy.getSum(expr);
                break;

            case Zserio4Parser.VALUEOF:
                formatting = policy.getValueOf(expr);
                break;

            case Zserio4Parser.EXPLICIT:
                formatting = policy.getExplicit(expr);
                break;

            case Zserio4Parser.NUMBITS:
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
            case Zserio4Parser.COMMA:
                formatting = policy.getComma(expr);
                break;

            case Zserio4Parser.LOGICAL_OR:
                formatting = policy.getLogicalOr(expr);
                break;

            case Zserio4Parser.LOGICAL_AND:
                formatting = policy.getLogicalAnd(expr);
                break;

            case Zserio4Parser.OR:
                formatting = policy.getOr(expr);
                break;

            case Zserio4Parser.XOR:
                formatting = policy.getXor(expr);
                break;

            case Zserio4Parser.AND:
                formatting = policy.getAnd(expr);
                break;

            case Zserio4Parser.EQ:
                formatting = policy.getEq(expr);
                break;

            case Zserio4Parser.NE:
                formatting = policy.getNe(expr);
                break;

            case Zserio4Parser.LT:
                formatting = policy.getLt(expr);
                break;

            case Zserio4Parser.LE:
                formatting = policy.getLe(expr);
                break;

            case Zserio4Parser.GE:
                formatting = policy.getGe(expr);
                break;

            case Zserio4Parser.GT:
                formatting = policy.getGt(expr);
                break;

            case Zserio4Parser.LSHIFT:
                formatting = policy.getLeftShift(expr);
                break;

            case Zserio4Parser.RSHIFT:
                formatting = policy.getRightShift(expr);
                break;

            case Zserio4Parser.PLUS:
                formatting = policy.getPlus(expr);
                break;

            case Zserio4Parser.MINUS:
                formatting = policy.getMinus(expr);
                break;

            case Zserio4Parser.MULTIPLY:
                formatting = policy.getMultiply(expr);
                break;

            case Zserio4Parser.DIVIDE:
                formatting = policy.getDivide(expr);
                break;

            case Zserio4Parser.MODULO:
                formatting = policy.getModulo(expr);
                break;

            case Zserio4Parser.LBRACKET:
                oldInFlag = inArray;
                inArray = true;
                final boolean isSetter = !inDot && formatSetter;
                formatting = policy.getArrayElement(expr, isSetter);
                break;

            case Zserio4Parser.DOT:
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

        if (expressionType == Zserio4Parser.LBRACKET)
            inArray = oldInFlag;
        else if (expressionType == Zserio4Parser.DOT)
            inDot = oldInFlag;

        append(expr.op2());
        buffer.append(formatting.getAfterOperand2());
    }

    private void emitTernaryExpression(Expression expr) throws ZserioEmitException
    {
        ExpressionFormattingPolicy.TernaryExpressionFormatting formatting;
        switch (expr.getType())
        {
            case Zserio4Parser.QUESTIONMARK:
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
