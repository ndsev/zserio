package zserio.emit.common;

import zserio.ast.Expression;

/**
 * Interface for expression formatting policy.
 *
 * The expression formatting policy is called from expression formatter to allow implement different formatting
 * for all emitters.
 */
public interface ExpressionFormattingPolicy
{
    /**
     * Class to represent unary expression formatting.
     *
     * Unary expression formatting has the following format:
     *
     * 'beforeOperand''UnaryOperand''afterOperand'
     *
     * Example:
     * !'UnaryOperand'
     */
    public static class UnaryExpressionFormatting
    {
        public UnaryExpressionFormatting(String beforeOperand)
        {
            this(beforeOperand, "");
        }

        public UnaryExpressionFormatting(String beforeOperand, String afterOperand)
        {
            this.beforeOperand = beforeOperand;
            this.afterOperand = afterOperand;
        }

        public String getBeforeOperand()
        {
            return beforeOperand;
        }

        public String getAfterOperand()
        {
            return afterOperand;
        }

        private final String    beforeOperand;
        private final String    afterOperand;
    }

    /**
     * Class to represent binary expression formatting.
     *
     * Binary expression formatting has the following format:
     *
     * 'beforeOperand1''BinaryOperand1''afterOperand1''BinaryOperand2''afterOperand2'
     *
     * Example:
     * 'BinaryOperand1' + 'BinaryOperand2'
     */
    public static class BinaryExpressionFormatting
    {
        public BinaryExpressionFormatting(String afterOperand1)
        {
            this("", afterOperand1, "");
        }

        public BinaryExpressionFormatting(String beforeOperand1, String afterOperand1,
                String afterOperand2)
        {
            this.beforeOperand1 = beforeOperand1;
            this.afterOperand1 = afterOperand1;
            this.afterOperand2 = afterOperand2;
        }

        public String getBeforeOperand1()
        {
            return beforeOperand1;
        }

        public String getAfterOperand1()
        {
            return afterOperand1;
        }

        public String getAfterOperand2()
        {
            return afterOperand2;
        }

        private final String    beforeOperand1;
        private final String    afterOperand1;
        private final String    afterOperand2;
    }

    /**
     * Class to represent ternary expression formatting.
     *
     * Ternary expression formatting has the following format:
     *
     * 'beforeOperand1''TernaryOperand1''afterOperand1''TernaryOperand2''afterOperand2''TernaryOperand3'
     * 'afterOperand3'
     *
     * Example:
     * ('TernaryOperand1') ? 'TernaryOperand2' : 'TernaryOperand3'
     */
    public static class TernaryExpressionFormatting
    {
        public TernaryExpressionFormatting(Expression expression, String beforeOperand1, String afterOperand1,
                String afterOperand2, String afterOperand3)
        {
            this.op1 = expression.op1();
            this.op2 = expression.op2();
            this.op3 = expression.op3();
            this.beforeOperand1 = beforeOperand1;
            this.afterOperand1 = afterOperand1;
            this.afterOperand2 = afterOperand2;
            this.afterOperand3 = afterOperand3;
        }

        public Expression getOperand1()
        {
            return op1;
        }

        public Expression getOperand2()
        {
            return op2;
        }

        public Expression getOperand3()
        {
            return op3;
        }

        public String getBeforeOperand1()
        {
            return beforeOperand1;
        }

        public String getAfterOperand1()
        {
            return afterOperand1;
        }

        public String getAfterOperand2()
        {
            return afterOperand2;
        }

        public String getAfterOperand3()
        {
            return afterOperand3;
        }

        private final Expression op1;
        private final Expression op2;
        private final Expression op3;
        private final String beforeOperand1;
        private final String afterOperand1;
        private final String afterOperand2;
        private final String afterOperand3;
    }

    // atom expressions formatting
    public String getDecimalLiteral(Expression expr, boolean isNegative);
    public String getBinaryLiteral(Expression expr, boolean isNegative);
    public String getHexadecimalLiteral(Expression expr, boolean isNegative);
    public String getOctalLiteral(Expression expr, boolean isNegative);
    public String getFloatLiteral(Expression expr, boolean isNegative);
    public String getDoubleLiteral(Expression expr, boolean isNegative);
    public String getBoolLiteral(Expression expr);
    public String getStringLiteral(Expression expr);
    public String getIndex(Expression expr);
    public String getIdentifier(Expression expr, boolean isLastInDot, boolean isSetter)
            throws ZserioEmitException;

    // unary expressions formatting
    public UnaryExpressionFormatting getBigIntegerCastingToNative(Expression expr);
    public UnaryExpressionFormatting getUnaryPlus(Expression expr);
    public UnaryExpressionFormatting getUnaryMinus(Expression expr);
    public UnaryExpressionFormatting getTilde(Expression expr);
    public UnaryExpressionFormatting getBang(Expression expr);
    public UnaryExpressionFormatting getLeftParenthesis(Expression expr);
    public UnaryExpressionFormatting getFunctionCall(Expression expr);
    public UnaryExpressionFormatting getLengthOf(Expression expr);
    public UnaryExpressionFormatting getSum(Expression expr);
    public UnaryExpressionFormatting getValueOf(Expression expr);
    public UnaryExpressionFormatting getNumBits(Expression expr);

    // binary expressions formatting
    public BinaryExpressionFormatting getComma(Expression expr);
    public BinaryExpressionFormatting getLogicalOr(Expression expr);
    public BinaryExpressionFormatting getLogicalAnd(Expression expr);
    public BinaryExpressionFormatting getOr(Expression expr);
    public BinaryExpressionFormatting getXor(Expression expr);
    public BinaryExpressionFormatting getAnd(Expression expr);
    public BinaryExpressionFormatting getEq(Expression expr);
    public BinaryExpressionFormatting getNe(Expression expr);
    public BinaryExpressionFormatting getLt(Expression expr);
    public BinaryExpressionFormatting getLe(Expression expr);
    public BinaryExpressionFormatting getGe(Expression expr);
    public BinaryExpressionFormatting getGt(Expression expr);
    public BinaryExpressionFormatting getLeftShift(Expression expr);
    public BinaryExpressionFormatting getRightShift(Expression expr);
    public BinaryExpressionFormatting getPlus(Expression expr);
    public BinaryExpressionFormatting getMinus(Expression expr);
    public BinaryExpressionFormatting getMultiply(Expression expr);
    public BinaryExpressionFormatting getDivide(Expression expr);
    public BinaryExpressionFormatting getModulo(Expression expr);
    public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter);
    public BinaryExpressionFormatting getDot(Expression expr);

    // ternary expressions formatting
    public TernaryExpressionFormatting getQuestionMark(Expression expr);
}
