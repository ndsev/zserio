package zserio.extension.cpp;

import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ExpressionFormattingPolicy;
import zserio.tools.ExtensionParameters;

final class TemplateDataContext
{
    public TemplateDataContext(ExtensionParameters extensionParameters)
    {
        cppNativeMapper = new CppNativeMapper();
        withWriterCode = extensionParameters.getWithWriterCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
    }

    public CppNativeMapper getCppNativeMapper()
    {
        return cppNativeMapper;
    }

    public ExpressionFormatter getExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppExpressionFormattingPolicy(cppNativeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getOwnerIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppOwnerIndirectExpressionFormattingPolicy(cppNativeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getSqlIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppSqlIndirectExpressionFormattingPolicy(cppNativeMapper, includeCollector);

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

    private final CppNativeMapper cppNativeMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
