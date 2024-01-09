package zserio.extension.common;

import zserio.antlr.ZserioParser;
import zserio.ast.Expression;

/**
 * Expression formatter used by all extensions.
 */
public final class ExpressionFormatter
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
     * @throws ZserioExtensionException Throws if expression has unexpected format.
     */
    public String formatSetter(Expression expr) throws ZserioExtensionException
    {
        return format(expr, true);
    }

    /**
     * Formats expression into string for getter accessors.
     *
     * @param expr Expression to format.
     *
     * @return Formatted expression in string format.
     *
     * @throws ZserioExtensionException Throws if expression has unexpected format.
     */
    public String formatGetter(Expression expr) throws ZserioExtensionException
    {
        return format(expr, false);
    }

    private String format(Expression expr, boolean formatSetter) throws ZserioExtensionException
    {
        buffer = new StringBuilder();
        this.formatSetter = formatSetter;
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

    private void append(Expression expr) throws ZserioExtensionException
    {
        if (!tryEmitExpressionAsStringValue(expr))
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
                throw new ZserioExtensionException(
                        "Expression with unexpected arity (" + arity + ") encountered!");
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

    private boolean tryEmitExpressionAsStringValue(Expression expr) throws ZserioExtensionException
    {
        if (!policy.getConfig().evaluateStrings())
            return false;

        if (expr.getStringValue() == null)
            return false;

        buffer.append(policy.getStringLiteral(expr));

        return true;
    }

    private void emitAtom(Expression expr, boolean isNegativeLiteral) throws ZserioExtensionException
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
            throw new ZserioExtensionException("Unknown atom expression type " + expr.getType() + "!");
        }
    }

    private void emitUnaryExpression(Expression expr) throws ZserioExtensionException
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

        case ZserioParser.VALUEOF:
            formatting = policy.getValueOf(expr);
            break;

        case ZserioParser.NUMBITS:
            formatting = policy.getNumBits(expr);
            break;

        default:
            throw new ZserioExtensionException("Unknown unary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand());
        append(expr.op1());
        buffer.append(formatting.getAfterOperand());
    }

    private void emitBinaryExpression(Expression expr) throws ZserioExtensionException
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

        case ZserioParser.ISSET:
            formatting = policy.getIsSet(expr);
            break;

        default:
            throw new ZserioExtensionException("Unknown binary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand1());
        append(expr.op1());
        buffer.append(formatting.getAfterOperand1());

        if (expressionType == ZserioParser.DOT)
            inDot = oldInFlag;

        append(expr.op2());

        if (expressionType == ZserioParser.LBRACKET)
            inArray = oldInFlag;

        buffer.append(formatting.getAfterOperand2());
    }

    private void emitTernaryExpression(Expression expr) throws ZserioExtensionException
    {
        ExpressionFormattingPolicy.TernaryExpressionFormatting formatting;
        switch (expr.getType())
        {
        case ZserioParser.QUESTIONMARK:
            formatting = policy.getQuestionMark(expr);
            break;

        default:
            throw new ZserioExtensionException("Unknown ternary expression type " + expr.getType() + "!");
        }

        buffer.append(formatting.getBeforeOperand1());
        final boolean oldFormatSetter = formatSetter;
        formatSetter = false; // condition in ternary operator must be always getter
        append(formatting.getOperand1());
        formatSetter = oldFormatSetter;
        buffer.append(formatting.getAfterOperand1());
        append(formatting.getOperand2());
        buffer.append(formatting.getAfterOperand2());
        append(formatting.getOperand3());
        buffer.append(formatting.getAfterOperand3());
    }

    private final ExpressionFormattingPolicy policy;

    private StringBuilder buffer;
    private boolean formatSetter;
    private boolean wasUnaryMinus;
    private boolean inDot;
    private boolean inArray;
}
