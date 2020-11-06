package zserio.emit.doc;

import zserio.emit.common.ExpressionFormatter;

class TemplateDataContext
{
    public TemplateDataContext(boolean withSvgDiagrams, UsedByCollector usedByCollector,
            DocResourceManager docResourceManager, String htmlContentDirectory,
            String symbolCollaborationDirectory)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
        this.docResourceManager = docResourceManager;
        this.htmlContentDirectory = htmlContentDirectory;
        this.symbolCollaborationDirectory = symbolCollaborationDirectory;
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

    public DocResourceManager getDocResourceManager()
    {
        return docResourceManager;
    }

    public String getHtmlContentDirectory()
    {
        return htmlContentDirectory;
    }

    public String getSymbolCollaborationDirectory()
    {
        return symbolCollaborationDirectory;
    }

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter docExpressionFormatter;
    private final DocResourceManager docResourceManager;
    private final String htmlContentDirectory;
    private final String symbolCollaborationDirectory;
}
