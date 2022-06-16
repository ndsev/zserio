package zserio.extension.java;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.Expression;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for bit size.
 */
public class BitSizeTemplateData
{
    public BitSizeTemplateData(String value)
    {
        this.value = value;
        this.lambdaValue = value;
        this.isDynamicBitField = false;
    }

    public BitSizeTemplateData(String value, String lambdaValue)
    {
        this.value = value;
        this.lambdaValue = lambdaValue;
        this.isDynamicBitField = true;
    }

    public String getValue()
    {
        return value;
    }

    public String getLambdaValue()
    {
        return lambdaValue;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    public static BitSizeTemplateData create(TypeInstantiation typeInstantiation,
            ExpressionFormatter javaExpressionFormatter, ExpressionFormatter javaLambdaExpressionFormatter)
                    throws ZserioExtensionException
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            return new BitSizeTemplateData(JavaLiteralFormatter.formatIntLiteral(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize()));
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final Expression lenExpr = ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression();
            return new BitSizeTemplateData(javaExpressionFormatter.formatGetter(lenExpr),
                    javaLambdaExpressionFormatter.formatGetter(lenExpr));
        }
        else
            return null;
    }

    private final String value;
    private final String lambdaValue;
    private final boolean isDynamicBitField;
}