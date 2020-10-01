package zserio.emit.doc;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.PackageMapper;

class TemplateDataContext
{
    public TemplateDataContext(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector,
            PackageMapper packageMapper)
    {
        this.outputPath = outputPath;
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
        this.symbolTemplateDataMapper = new SymbolTemplateDataMapper(packageMapper, ".");
    }

    public String getOutputPath()
    {
        return outputPath;
    }

    public boolean getWithSvgDiagrams()
    {
        return withSvgDiagrams;
    }

    public UsedByCollector getUsedByCollector()
    {
        return usedByCollector;
    }

    public ExpressionFormatter getExpressionFormatter()
    {
        return docExpressionFormatter;
    }

    public SymbolTemplateDataMapper getSymbolTemplateDataMapper()
    {
        return symbolTemplateDataMapper;
    }

    private final String outputPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter docExpressionFormatter;
    private final SymbolTemplateDataMapper symbolTemplateDataMapper;
}
