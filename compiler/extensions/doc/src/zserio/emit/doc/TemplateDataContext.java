package zserio.emit.doc;

import java.io.File;

import zserio.ast.Package;
import zserio.emit.common.ExpressionFormatter;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

class TemplateDataContext
{
    // TODO[mikir] To split out DocResourceManager and put it only to PackageEmitter?!?
    public TemplateDataContext(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, Package rootPackage, String htmlRootDirectory)
    {
        this(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector, rootPackage,
                htmlRootDirectory, "");
    }

    public TemplateDataContext(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, Package rootPackage, String htmlRootDirectory,
            String htmlCurrentDirectory)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
        this.docResourceManager = new DocResourceManager(outputPathName, extensionParameters,
                DocDefaultEmitter.CONTENT_DIRECTORY, rootPackage);

        contentDirectory = getHtmlDirectory(htmlRootDirectory, htmlCurrentDirectory,
                DocDefaultEmitter.CONTENT_DIRECTORY);
        cssDirectory = getHtmlDirectory(htmlRootDirectory, htmlCurrentDirectory,
                DocDefaultEmitter.CSS_DIRECTORY);
        jsDirectory = getHtmlDirectory(htmlRootDirectory, htmlCurrentDirectory,
                DocDefaultEmitter.JS_DIRECTORY);
        resourcesDirectory = getHtmlDirectory(htmlRootDirectory, htmlCurrentDirectory,
                DocDefaultEmitter.RESOURCES_DIRECTORY);
        symbolCollaborationDirectory = getHtmlDirectory(htmlRootDirectory, htmlCurrentDirectory,
                DocDefaultEmitter.SYMBOL_COLLABORATION_DIRECTORY);
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

    public String getContentDirectory()
    {
        return contentDirectory;
    }

    public String getCssDirectory()
    {
        return cssDirectory;
    }

    public String getJsDirectory()
    {
        return jsDirectory;
    }

    public String getResourcesDirectory()
    {
        return resourcesDirectory;
    }

    public String getSymbolCollaborationDirectory()
    {
        return symbolCollaborationDirectory;
    }

    private static String getHtmlDirectory(String htmlRootDirectory, String htmlCurrentDirectory,
            String htmlSubdirectory)
    {
        if (htmlSubdirectory.equals(htmlCurrentDirectory))
            return ".";

        return StringJoinUtil.joinStrings(htmlRootDirectory, htmlSubdirectory, File.separator);
    }

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final ExpressionFormatter docExpressionFormatter;
    private final DocResourceManager docResourceManager;

    private final String contentDirectory;
    private final String cssDirectory;
    private final String jsDirectory;
    private final String resourcesDirectory;
    private final String symbolCollaborationDirectory;
}
