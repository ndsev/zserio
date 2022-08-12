package zserio.ast;

import java.util.ArrayList;
import java.util.List;

import zserio.antlr.ZserioParser;
import zserio.extension.common.DefaultExpressionFormattingPolicy;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * The class compares expressions using resolved fields.
 */
class ExpressionComparator
{
    /**
     * Compares two expressions using resolved fields.
     *
     * @param expr1 The first expression to compare.
     * @param expr2 The second expression to compare.
     *
     * @return true if expressions are the same using resolved fields, otherwise false.
     */
    public static boolean equals(Expression expr1, Expression expr2)
    {
        return equals(expr1, expr2, new ArrayList<AstNode>());
    }

    /**
     * Compares two expressions accepting dot prefix using resolved fields.
     *
     * Note:
     *
     * All fields, functions and parameters referenced in the second expression expr2 are prefixed
     * by dotPrefix2 before comparison.
     *
     * @param expr1 The first expression to compare.
     * @param expr2 The second expression to compare.
     * @param dotPrefix2 Dot prefix for the second expression.
     *
     * @return true if expressions are the same using resolved fields, otherwise false.
     */
    public static boolean equals(Expression expr1, Expression expr2, List<AstNode> dotPrefix2)
    {
        try
        {
            final ExpressionFormatter expressionFormatter1 = new ExpressionFormatter(
                    new ComparisonExpressionFormattingPolicy(new ArrayList<AstNode>()));
            final ExpressionFormatter expressionFormatter2 = new ExpressionFormatter(
                    new ComparisonExpressionFormattingPolicy(dotPrefix2));
            final String formattedExpr1 = expressionFormatter1.formatGetter(removeParentheses(expr1));
            final String formattedExpr2 = expressionFormatter2.formatGetter(removeParentheses(expr2));
            if (formattedExpr1.equals(formattedExpr2))
                return true;
        }
        catch (ZserioExtensionException e)
        {
            // differs in case of any error
        }

        return false;
    }

    private static Expression removeParentheses(Expression expr)
    {
        Expression currentExpr = expr;
        while (currentExpr.getType() == ZserioParser.LPAREN)
            currentExpr = currentExpr.op1();

        return currentExpr;
    }

    private static class ComparisonExpressionFormattingPolicy extends DefaultExpressionFormattingPolicy
    {
        public ComparisonExpressionFormattingPolicy(List<AstNode> dotPrefix)
        {
            final StringBuilder formattedDotPrefix = new StringBuilder();
            for (AstNode dotSymbol : dotPrefix)
            {
                formattedDotPrefix.append(formatSymbol(dotSymbol));
                formattedDotPrefix.append(ZSERIO_DOT_SEPARATOR);
            }

            this.formattedDotPrefix = formattedDotPrefix.toString();
        }

        @Override
        public String getDecimalLiteral(Expression expr, boolean isNegative)
        {
            return expr.getText();
        }

        @Override
        public String getBinaryLiteral(Expression expr, boolean isNegative)
        {
            return expr.getText() + ZSERIO_BINARY_LITERAL_SUFFIX;
        }

        @Override
        public String getHexadecimalLiteral(Expression expr, boolean isNegative)
        {
            return ZSERIO_HEXADECIMAL_LITERAL_PREFIX + expr.getText();
        }

        @Override
        public String getOctalLiteral(Expression expr, boolean isNegative)
        {
            return ZSERIO_OCTAL_LITERAL_PREFIX + expr.getText();
        }

        @Override
        public String getFloatLiteral(Expression expr, boolean isNegative)
        {
            return expr.getText() + ZSERIO_FLOAT_LITERAL_SUFFIX;
        }

        @Override
        public String getDoubleLiteral(Expression expr, boolean isNegative)
        {
            return expr.getText();
        }

        @Override
        public String getBoolLiteral(Expression expr)
        {
            return expr.getText();
        }

        @Override
        public String getStringLiteral(Expression expr)
        {
            return expr.getText();
        }

        @Override
        public String getIndex(Expression expr)
        {
            return "@index";
        }

        @Override
        public String getIdentifier(Expression expr, boolean isLastInDot, boolean isSetter)
        {
            final StringBuilder result = new StringBuilder();

            if (expr.isExplicitVariable())
                result.append("explicit ");

            final AstNode resolvedSymbol = expr.getExprSymbolObject();
            if (expr.isMostLeftId())
            {
                if (resolvedSymbol instanceof Field || resolvedSymbol instanceof Function ||
                        resolvedSymbol instanceof Parameter)
                {
                    // this identifier has been resolved to symbol in inner scope => put dot prefix
                    result.append(formattedDotPrefix);
                }
            }

            result.append(formatSymbol(resolvedSymbol));

            return result.toString();
        }

        @Override
        public UnaryExpressionFormatting getFunctionCall(Expression expr)
        {
            return new UnaryExpressionFormatting("", "()");
        }

        @Override
        public UnaryExpressionFormatting getLengthOf(Expression expr)
        {
            return new UnaryExpressionFormatting("lengthof(" , ")");
        }

        @Override
        public UnaryExpressionFormatting getValueOf(Expression expr)
        {
            return new UnaryExpressionFormatting("valueof(" , ")");
        }

        @Override
        public UnaryExpressionFormatting getNumBits(Expression expr)
        {
            return new UnaryExpressionFormatting("numbits(", ")");
        }

        @Override
        public BinaryExpressionFormatting getArrayElement(Expression expr, boolean isSetter)
        {
            return new BinaryExpressionFormatting("", "[", "]");
        }

        @Override
        public BinaryExpressionFormatting getDot(Expression expr)
        {
            // wait for whole dot expression
            return new BinaryExpressionFormatting(ZSERIO_DOT_SEPARATOR);
        }

        @Override
        public BinaryExpressionFormatting getIsSet(Expression expr)
        {
            return new BinaryExpressionFormatting("isset(", ", ", ")");
        }

        private static String formatSymbol(AstNode symbol)
        {
            return symbol.getClass().getSimpleName() + "@" + symbol.hashCode();
        }

        private final static String ZSERIO_BINARY_LITERAL_SUFFIX = "b";
        private final static String ZSERIO_HEXADECIMAL_LITERAL_PREFIX = "0x";
        private final static String ZSERIO_OCTAL_LITERAL_PREFIX = "0";
        private final static String ZSERIO_FLOAT_LITERAL_SUFFIX = "f";

        private final static String ZSERIO_DOT_SEPARATOR = ".";

        private final String formattedDotPrefix;
    }
}
