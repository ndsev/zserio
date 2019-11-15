package zserio.emit.cpp98;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

final class TemplateDataContext
{
    public TemplateDataContext(Parameters extensionParameters, PackageMapper cppPackageMapper)
    {
        cppNativeMapper = new CppNativeMapper(cppPackageMapper);
        this.cppPackageMapper = cppPackageMapper;
        withWriterCode = extensionParameters.getWithWriterCode();
        withInspectorCode = extensionParameters.getWithInspectorCode();
        withValidationCode = extensionParameters.getWithValidationCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
    }

    public CppNativeMapper getCppNativeMapper()
    {
        return cppNativeMapper;
    }

    public PackageMapper getCppPackageMapper()
    {
        return cppPackageMapper;
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

    public boolean getWithInspectorCode()
    {
        return withInspectorCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    private final CppNativeMapper cppNativeMapper;
    private final PackageMapper cppPackageMapper;

    private final boolean withWriterCode;
    private final boolean withInspectorCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
}
