package zserio.extension.cpp;

import zserio.ast.DynamicBitFieldInstantiation;
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
        this.ownerIndirectValue = null;
        this.objectIndirectValue = null;
        this.rowIndirectValue = null;
        this.isDynamicBitField = false;
    }

    public BitSizeTemplateData(String value, String ownerIndirectValue, String objectIndirectValue,
            String rowIndirectValue)
    {
        this.value = value;
        this.ownerIndirectValue = ownerIndirectValue;
        this.objectIndirectValue = objectIndirectValue;
        this.rowIndirectValue = rowIndirectValue;
        this.isDynamicBitField = true;
    }

    public String getValue()
    {
        return value;
    }

    public String getOwnerIndirectValue()
    {
        return ownerIndirectValue;
    }

    public String getObjectIndirectValue()
    {
        return objectIndirectValue;
    }

    public String getRowIndirectValue()
    {
        return rowIndirectValue;
    }

    public boolean getIsDynamicBitField()
    {
        return isDynamicBitField;
    }

    public static BitSizeTemplateData create(TemplateDataContext context, TypeInstantiation typeInstantiation,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        if (typeInstantiation.getBaseType() instanceof FixedSizeType)
        {
            final String value = CppLiteralFormatter.formatUInt8Literal(
                    ((FixedSizeType)typeInstantiation.getBaseType()).getBitSize());
            return new BitSizeTemplateData(value);
        }
        else if (typeInstantiation instanceof DynamicBitFieldInstantiation)
        {
            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            final String value = cppExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());

            final ExpressionFormatter cppOwnerIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "m_ownerRef.get()");
            final String ownerIndirectValue = cppOwnerIndirectExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());

            final ExpressionFormatter cppObjectIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "m_object");
            final String objectIndirectValue = cppObjectIndirectExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());

            final ExpressionFormatter cppRowIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "row");
            final String rowIndirectValue = cppRowIndirectExpressionFormatter.formatGetter(
                    ((DynamicBitFieldInstantiation)typeInstantiation).getLengthExpression());

            return new BitSizeTemplateData(value, ownerIndirectValue, objectIndirectValue, rowIndirectValue);
        }
        else
        {
            return null;
        }
    }

    private final String value;
    private final String ownerIndirectValue;
    private final String objectIndirectValue;
    private final String rowIndirectValue;
    private final boolean isDynamicBitField;
}
