package zserio.extension.python;

import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ExpressionFormattingPolicy;

final class TemplateDataContext
{
    public TemplateDataContext(PythonExtensionParameters pythonParameters)
    {
        pythonNativeMapper = new PythonNativeMapper();
        withWriterCode = pythonParameters.getWithWriterCode();
        withRangeCheckCode = pythonParameters.getWithRangeCheckCode();
        withPythonPropPrefix = pythonParameters.getWithPythonPropPrefix();
    }

    public PythonNativeMapper getPythonNativeMapper()
    {
        return pythonNativeMapper;
    }

    public ExpressionFormatter getPythonExpressionFormatter(ImportCollector importCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new PythonExpressionFormattingPolicy(pythonNativeMapper, importCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getPythonSqlIndirectExpressionFormatter(ImportCollector importCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new PythonSqlIndirectExpressionFormattingPolicy(pythonNativeMapper, importCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getWithPythonPropPrefix()
    {
        return withPythonPropPrefix;
    }

    private final PythonNativeMapper pythonNativeMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
    private final boolean withPythonPropPrefix;
}
