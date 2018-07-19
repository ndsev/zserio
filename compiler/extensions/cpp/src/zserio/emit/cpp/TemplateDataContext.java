package zserio.emit.cpp;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.emit.common.PackageMapper;

final class TemplateDataContext
{
    public TemplateDataContext(CppNativeTypeMapper cppNativeTypeMapper,
            PackageMapper cppPackageMapper, boolean withWriterCode, boolean withInspectorCode,
            boolean withValidationCode, boolean withRangeCheckCode)
    {
        this.cppNativeTypeMapper = cppNativeTypeMapper;
        this.cppPackageMapper = cppPackageMapper;
        this.withWriterCode = withWriterCode;
        this.withInspectorCode = withInspectorCode;
        this.withValidationCode = withValidationCode;
        this.withRangeCheckCode = withRangeCheckCode;
    }

    public CppNativeTypeMapper getCppNativeTypeMapper()
    {
        return cppNativeTypeMapper;
    }

    public PackageMapper getCppPackageMapper()
    {
        return cppPackageMapper;
    }

    public ExpressionFormatter getExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppExpressionFormattingPolicy(cppNativeTypeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getOwnerIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppOwnerIndirectExpressionFormattingPolicy(cppNativeTypeMapper, includeCollector);

        return new ExpressionFormatter(expressionFormattingPolicy);
    }

    public ExpressionFormatter getSqlIndirectExpressionFormatter(IncludeCollector includeCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new CppSqlIndirectExpressionFormattingPolicy(cppNativeTypeMapper, includeCollector);

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

    private final CppNativeTypeMapper   cppNativeTypeMapper;
    private final PackageMapper         cppPackageMapper;

    private final boolean withWriterCode;
    private final boolean withInspectorCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
}
