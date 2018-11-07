package zserio.emit.python;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ExpressionFormattingPolicy;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

final class TemplateDataContext
{
    public TemplateDataContext(Parameters extensionParameters, PackageMapper pythonPackageMapper)
    {
        pythonNativeTypeMapper = new PythonNativeTypeMapper(pythonPackageMapper);
        this.pythonPackageMapper = pythonPackageMapper;
        withWriterCode = extensionParameters.getWithWriterCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
    }

    public PythonNativeTypeMapper getPythonNativeTypeMapper()
    {
        return pythonNativeTypeMapper;
    }

    public PackageMapper getPythonPackageMapper()
    {
        return pythonPackageMapper;
    }

    public ExpressionFormatter getExpressionFormatter(ImportCollector importCollector)
    {
        final ExpressionFormattingPolicy expressionFormattingPolicy =
                new PythonExpressionFormattingPolicy(pythonNativeTypeMapper, importCollector);

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

    private final PythonNativeTypeMapper pythonNativeTypeMapper;
    private final PackageMapper pythonPackageMapper;

    private final boolean withWriterCode;
    private final boolean withRangeCheckCode;
}
