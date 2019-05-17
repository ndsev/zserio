package zserio.emit.doc;

import zserio.ast.ZserioType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Parameter;
import zserio.emit.common.DefaultExpressionFormattingPolicy;

public class DocExpressionFormattingPolicy extends DefaultExpressionFormattingPolicy
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
        String symbol = expr.getText();
        String res = (expr.isExplicitVariable()) ? "explicit " : "";

        Object obj = expr.getExprSymbolObject();
        if (obj instanceof EnumType)
        {
            EnumType enumeration = (EnumType) obj;
            res += enumeration.getName();
        }
        else if (obj instanceof ZserioType)
        {
            res += symbol;
        }
        else if (obj instanceof Parameter)
        {
            Parameter param = (Parameter) obj;
            String pName = param.getName();
            res += pName;
        }
        else if (obj instanceof Field)
        {
            Field field = (Field) obj;
            res += field.getName();
        }
        else if (obj instanceof EnumItem)
        {
            EnumItem item = (EnumItem) obj;
            String value = item.getName();
            res += value;
        }
        else
        {
            res += symbol;
        }

        return res;
    }

    @Override
    public UnaryExpressionFormatting getFunctionCall(Expression expr)
    {
        return new UnaryExpressionFormatting("", "()");
    }

    @Override
    public UnaryExpressionFormatting getLengthOf(Expression expr)
    {
        return new UnaryExpressionFormatting("lengthof(" + ")");
    }

    @Override
    public UnaryExpressionFormatting getSum(Expression expr)
    {
        return new UnaryExpressionFormatting("sum(", ")");
    }

    @Override
    public UnaryExpressionFormatting getValueOf(Expression expr)
    {
        return new UnaryExpressionFormatting("valueof(" + ")");
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

    private final static String DOC_BINARY_LITERAL_SUFFIX = "b";
    private final static String DOC_HEXADECIMAL_LITERAL_PREFIX = "0x";
    private final static String DOC_OCTAL_LITERAL_PREFIX = "0";
    private final static String DOC_FLOAT_LITERAL_SUFFIX = "f";
}
