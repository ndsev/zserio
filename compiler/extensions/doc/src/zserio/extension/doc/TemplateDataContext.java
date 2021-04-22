package zserio.extension.doc;

import java.io.File;

import zserio.extension.common.ExpressionFormatter;
import zserio.tools.StringJoinUtil;

/**
 * Freemarker template data context for all emitters.
 *
 * Freemarker template data context holds all parameters used by Freemarker template data.
 */
class TemplateDataContext
{
    public TemplateDataContext(DocExtensionParameters docParameters, String htmlRootDirectory)
    {
        this.withSvgDiagrams = docParameters.getWithSvgDiagrams();
        this.docExpressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());

        contentDirectory = getHtmlDirectory(htmlRootDirectory, DocDirectories.CONTENT_DIRECTORY);
        packagesDirectory = getHtmlDirectory(htmlRootDirectory, DocDirectories.PACKAGES_DIRECTORY);
        cssDirectory = getHtmlDirectory(htmlRootDirectory, DocDirectories.CSS_DIRECTORY);
        jsDirectory = getHtmlDirectory(htmlRootDirectory, DocDirectories.JS_DIRECTORY);
        resourcesDirectory = getHtmlDirectory(htmlRootDirectory, DocDirectories.RESOURCES_DIRECTORY);
        symbolCollaborationDirectory = getHtmlDirectory(htmlRootDirectory,
                DocDirectories.SYMBOL_COLLABORATION_DIRECTORY);
    }

    public boolean getWithSvgDiagrams()
    {
        return withSvgDiagrams;
    }

    public ExpressionFormatter getExpressionFormatter()
    {
        return docExpressionFormatter;
    }

    public String getContentDirectory()
    {
        return contentDirectory;
    }

    public String getPackagesDirectory()
    {
        return packagesDirectory;
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

    private static String getHtmlDirectory(String htmlRootDirectory, String htmlSubdirectory)
    {
        return StringJoinUtil.joinStrings(htmlRootDirectory, htmlSubdirectory, File.separator);
    }

    private final boolean withSvgDiagrams;
    private final ExpressionFormatter docExpressionFormatter;

    private final String contentDirectory;
    private final String packagesDirectory;
    private final String cssDirectory;
    private final String jsDirectory;
    private final String resourcesDirectory;
    private final String symbolCollaborationDirectory;
}
