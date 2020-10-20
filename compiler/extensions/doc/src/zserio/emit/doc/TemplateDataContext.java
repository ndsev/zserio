package zserio.emit.doc;

import zserio.emit.common.ExpressionFormatter;

class TemplateDataContext
{
    public TemplateDataContext(boolean withSvgDiagrams, UsedByCollector usedByCollector,
            ResourceManager resourceManager, String htmlContentDirectory, String symbolCollaborationDirectory,
            String dbStructureDirectory)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
        this.resourceManager = resourceManager;
        this.htmlContentDirectory = htmlContentDirectory;
        this.symbolCollaborationDirectory = symbolCollaborationDirectory;
        this.dbStructureDirectory = dbStructureDirectory;
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

    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    public String getHtmlContentDirectory()
    {
        return htmlContentDirectory;
    }

    public String getSymbolCollaborationDirectory()
    {
        return symbolCollaborationDirectory;
    }

    public String getDbStructureDirectory()
    {
        return dbStructureDirectory;
    }

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter docExpressionFormatter;
    private final ResourceManager resourceManager;
    private final String htmlContentDirectory;
    private final String symbolCollaborationDirectory;
    private final String dbStructureDirectory;
}
