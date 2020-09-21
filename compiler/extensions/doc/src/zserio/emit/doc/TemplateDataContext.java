package zserio.emit.doc;

import zserio.emit.common.ExpressionFormatter;

public class TemplateDataContext
{
    public TemplateDataContext(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        this.outputPath = outputPath;
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
        this.expressionFormatter = new ExpressionFormatter(policy);
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
        return expressionFormatter;
    }

    private final String outputPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter expressionFormatter;
}