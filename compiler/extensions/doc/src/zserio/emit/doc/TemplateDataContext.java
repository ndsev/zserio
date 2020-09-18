package zserio.emit.doc;

public class TemplateDataContext
{
    public TemplateDataContext(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        this.outputPath = outputPath;
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
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

    private final String outputPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
}