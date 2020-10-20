package zserio.emit.python;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.tools.Parameters;

final class TemplateDataContext
{
    public TemplateDataContext(Parameters extensionParameters)
    {
        pythonNativeMapper = new PythonNativeMapper();
        withWriterCode = extensionParameters.getWithWriterCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
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

    private final PythonNativeMapper pythonNativeMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
