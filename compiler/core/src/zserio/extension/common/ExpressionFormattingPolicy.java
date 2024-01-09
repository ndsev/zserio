package zserio.extension.common;

import zserio.ast.Expression;

/**
 * Interface for expression formatting policy.
 *
 * The expression formatting policy is called from expression formatter to allow implement different formatting
 * for all extensions.
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
    public static final class UnaryExpressionFormatting
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

        private final String beforeOperand;
        private final String afterOperand;
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
    public static final class BinaryExpressionFormatting
    {
        public BinaryExpressionFormatting(String afterOperand1)
        {
            this("", afterOperand1, "");
        }

        public BinaryExpressionFormatting(String beforeOperand1, String afterOperand1, String afterOperand2)
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

        private final String beforeOperand1;
        private final String afterOperand1;
        private final String afterOperand2;
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

    /**
     * Interface to formatting configuration.
     */
    public static interface FormattingConfig {
        /**
         * Gets whether constant string expressions should be evaluated.
         *
         * @return True when constant string expressions should be evaluated, false otherwise.
         */
        public boolean evaluateStrings();
    }

    /**
     * Gets formatting configuration.
     *
     * @return Formatting configuration.
     */
    public FormattingConfig getConfig();

    // atom expressions formatting
    public String getDecimalLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getBinaryLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getHexadecimalLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getOctalLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getFloatLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getDoubleLiteral(Expression expr, boolean isNegative) throws ZserioExtensionException;
    public String getBoolLiteral(Expression expr) throws ZserioExtensionException;
    public String getStringLiteral(Expression expr) throws ZserioExtensionException;
    public String getIndex(Expression expr) throws ZserioExtensionException;
    public String getIdentifier(Expression expr, boolean isLastInDot, boolean isSetter)
            throws ZserioExtensionException;

    // unary expressions formatting
    public UnaryExpressionFormatting getBigIntegerCastingToNative(Expression expr)
            throws ZserioExtensionException;
    public UnaryExpressionFormatting getUnaryPlus(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getUnaryMinus(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getTilde(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getBang(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getLeftParenthesis(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getFunctionCall(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getLengthOf(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getValueOf(Expression expr) throws ZserioExtensionException;
    public UnaryExpressionFormatting getNumBits(Expression expr) throws ZserioExtensionException;

    // binary expressions formatting
    public BinaryExpressionFormatting getComma(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getLogicalOr(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getLogicalAnd(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getOr(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getXor(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getAnd(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getEq(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getNe(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getLt(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getLe(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getGe(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getGt(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getLeftShift(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getRightShift(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getPlus(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getMinus(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getMultiply(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getDivide(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getModulo(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter)
            throws ZserioExtensionException;
    public BinaryExpressionFormatting getDot(Expression expr) throws ZserioExtensionException;
    public BinaryExpressionFormatting getIsSet(Expression expr) throws ZserioExtensionException;

    // ternary expressions formatting
    public TernaryExpressionFormatting getQuestionMark(Expression expr) throws ZserioExtensionException;
}
