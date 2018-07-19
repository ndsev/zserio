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
        return expr.getText();
    }

    @Override
    public String getHexadecimalLiteral(Expression expr, boolean isNegative)
    {
        return expr.getText();
    }

    @Override
    public String getOctalLiteral(Expression expr, boolean isNegative)
    {
        return expr.getText();
    }

    @Override
    public String getFloatLiteral(Expression expr, boolean isNegative)
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
    public String getIdentifier(Expression expr, boolean isLast, boolean isSetter)
    {
        String symbol = expr.getText();
        String res = "";

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
        return new UnaryExpressionFormatting("lengthof ");
    }

    @Override
    public UnaryExpressionFormatting getSum(Expression expr)
    {
        return new UnaryExpressionFormatting("sum(", ")");
    }

    @Override
    public UnaryExpressionFormatting getExplicit(Expression expr)
    {
        return new UnaryExpressionFormatting("explicit ");
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
}
