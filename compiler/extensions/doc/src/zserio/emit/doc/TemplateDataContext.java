package zserio.emit.doc;

import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.PackageMapper;

class TemplateDataContext
{
    public TemplateDataContext(boolean withSvgDiagrams, UsedByCollector usedByCollector,
            PackageMapper packageMapper, ResourceManager resourceManager, String htmlContentDirectory,
            String typeCollaborationDirectory)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
        this.packageMapper = packageMapper;
        this.resourceManager = resourceManager;
        this.htmlContentDirectory = htmlContentDirectory;
        this.typeCollaborationDirectory = typeCollaborationDirectory;
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

    public PackageMapper getPackageMapper()
    {
        return packageMapper;
    }

    public ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    public String getHtmlContentDirectory()
    {
        return htmlContentDirectory;
    }

    public String getTypeCollaborationDirectory()
    {
        return typeCollaborationDirectory;
    }

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter docExpressionFormatter;
    private final PackageMapper packageMapper;
    private final ResourceManager resourceManager;
    private final String htmlContentDirectory;
    private final String typeCollaborationDirectory;
}
