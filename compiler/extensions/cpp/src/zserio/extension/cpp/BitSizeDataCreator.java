package zserio.extension.cpp;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public class BitSizeDataCreator
{
    public static BitSizeTemplateData createData(TypeInstantiation typeInstantiation,
            ExpressionFormatter cppExpressionFormatter) throws ZserioExtensionException
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            final String value = CppLiteralFormatter.formatUInt8Literal(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            return new BitSizeTemplateData(value, false);
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final String value = cppExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());
            return new BitSizeTemplateData(value, true);
        }
        else
        {
            return null;
        }
    }
}
