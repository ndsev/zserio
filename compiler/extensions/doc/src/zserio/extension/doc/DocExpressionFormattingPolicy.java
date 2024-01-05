package zserio.extension.doc;

import zserio.ast.Expression;
import zserio.extension.common.DefaultExpressionFormattingPolicy;

/**
 * Expression formatting policy for documentation extension.
 *
 * The class formats expressions for documentation extension.
 */
final class DocExpressionFormattingPolicy extends DefaultExpressionFormattingPolicy
{
    @Override
    public String getDecimalLiteral(Expression expr, boolean isNegative)
    {
        return expr.getText();
    }

    @Override
    public String getBinaryLiteral(Expression expr, boolean isNegative)
    {
        return expr.getText() + DOC_BINARY_LITERAL_SUFFIX;
    }

    @Override
    public String getHexadecimalLiteral(Expression expr, boolean isNegative)
    {
        return DOC_HEXADECIMAL_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getOctalLiteral(Expression expr, boolean isNegative)
    {
        return DOC_OCTAL_LITERAL_PREFIX + expr.getText();
    }

    @Override
    public String getFloatLiteral(Expression expr, boolean isNegative)
    {
        return expr.getText() + DOC_FLOAT_LITERAL_SUFFIX;
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
        final String symbol = expr.getText();

        return (expr.isExplicitVariable()) ? "explicit " + symbol : symbol;
    }

    @Override
    public UnaryExpressionFormatting getFunctionCall(Expression expr)
    {
        return new UnaryExpressionFormatting("", "()");
    }

    @Override
    public UnaryExpressionFormatting getLengthOf(Expression expr)
    {
        return new UnaryExpressionFormatting("lengthof(", ")");
    }

    @Override
    public UnaryExpressionFormatting getValueOf(Expression expr)
    {
        return new UnaryExpressionFormatting("valueof(", ")");
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
        return new BinaryExpressionFormatting(".");
    }

    @Override
    public BinaryExpressionFormatting getIsSet(Expression expr)
    {
        return new BinaryExpressionFormatting("isset(", ", ", ")");
    }

    private final static String DOC_BINARY_LITERAL_SUFFIX = "b";
    private final static String DOC_HEXADECIMAL_LITERAL_PREFIX = "0x";
    private final static String DOC_OCTAL_LITERAL_PREFIX = "0";
    private final static String DOC_FLOAT_LITERAL_SUFFIX = "f";
}
