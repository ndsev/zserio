package zserio.emit.cpp;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

final class TemplateDataContext
{
    public TemplateDataContext(Parameters extensionParameters, PackageMapper cppPackageMapper)
    {
        cppNativeTypeMapper = new CppNativeTypeMapper(cppPackageMapper);
        this.cppPackageMapper = cppPackageMapper;
        withWriterCode = extensionParameters.getWithWriterCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
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

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    private final CppNativeTypeMapper cppNativeTypeMapper;
    private final PackageMapper cppPackageMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
