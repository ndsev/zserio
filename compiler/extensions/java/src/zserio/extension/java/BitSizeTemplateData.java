package zserio.extension.java;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public class BitSizeTemplateData
{
    public BitSizeTemplateData(String value, boolean isDynamicBitField)
    {
        this.value = value;
        this.isDynamicBitField = isDynamicBitField;
    }

    public String getValue()
    {
        return value;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    public static BitSizeTemplateData create(TypeInstantiation typeInstantiation,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            return new BitSizeTemplateData(JavaLiteralFormatter.formatIntLiteral(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize()), false);
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                    (DynamicBitFieldInstantiation)typeInstantiation;
            return new BitSizeTemplateData(javaExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression()), true);
        }
        else
            return null;
    }

    private final String value;
    private final boolean isDynamicBitField;
}