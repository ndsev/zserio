package zserio.extension.cpp;

import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.FixedSizeType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for bit size.
 */
public final class BitSizeTemplateData
{
    public BitSizeTemplateData(String value)
    {
        this.value = value;
        this.ownerIndirectValue = null;
        this.objectIndirectValue = null;
        this.rowIndirectValue = null;
        this.needsOwner = false;
        this.isDynamicBitField = false;
    }

    public BitSizeTemplateData(String value, String ownerIndirectValue, String objectIndirectValue,
            String rowIndirectValue, boolean needsOwner)
    {
        this.value = value;
        this.ownerIndirectValue = ownerIndirectValue;
        this.objectIndirectValue = objectIndirectValue;
        this.rowIndirectValue = rowIndirectValue;
        this.needsOwner = needsOwner;
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

    public boolean getNeedsOwner()
    {
        return needsOwner;
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
            final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                    (DynamicBitFieldInstantiation)typeInstantiation;

            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            final String value = cppExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());

            final ExpressionFormatter cppOwnerIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "owner");
            final String ownerIndirectValue = cppOwnerIndirectExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());

            final ExpressionFormatter cppObjectIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "m_object");
            final String objectIndirectValue = cppObjectIndirectExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());

            final ExpressionFormatter cppRowIndirectExpressionFormatter =
                    context.getIndirectExpressionFormatter(includeCollector, "row");
            final String rowIndirectValue = cppRowIndirectExpressionFormatter.formatGetter(
                    dynamicBitFieldInstantiation.getLengthExpression());

            final boolean needsOwner = dynamicBitFieldInstantiation.getLengthExpression().requiresOwnerContext();

            return new BitSizeTemplateData(value, ownerIndirectValue, objectIndirectValue, rowIndirectValue,
                    needsOwner);
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
    private final boolean needsOwner;
    private final boolean isDynamicBitField;
}
