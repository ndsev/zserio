package zserio.extension.python;

import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ExpressionFormattingPolicy;
import zserio.extension.common.PackedTypesCollector;

/**
 * Template data context for structures which creates proper formatting policy.
 */
public final class StructTemplateDataContext extends TemplateDataContext
{
    public StructTemplateDataContext(
            PythonExtensionParameters pythonParameters, PackedTypesCollector packedTypesCollector)
    {
        super(pythonParameters, packedTypesCollector);
    }

    public ExpressionFormatter getPythonExpressionFormatter(ImportCollector importCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new PythonStructExpressionFormattingPolicy(this, importCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getPythonOwnerIndirectExpressionFormatter(ImportCollector importCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new PythonStructExpressionFormattingPolicy(
                        this, importCollector, TemplateDataContext.PYTHON_OWNER_PREFIX);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }
}
