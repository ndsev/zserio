package zserio.extension.doc;

import zserio.ast.Package;

/**
 * FreeMarker template data for HTML resource emitter.
 */
public final class HtmlResourceTemplateData
{
    public HtmlResourceTemplateData(TemplateDataContext context, Package rootPackage, boolean hasSchemaRules,
            String title, String bodyContent)
    {
        this.cssDirectory = context.getCssDirectory();
        this.stylesheetName = StylesheetEmitter.STYLESHEET_FILE_NAME;
        this.headerNavigation = new HeaderNavigationTemplateData(context, rootPackage, hasSchemaRules,
                HeaderNavigationTemplateData.ActiveItem.NO_ITEM);

        this.title = title;
        this.bodyContent = bodyContent;
    }

    public String getCssDirectory()
    {
        return cssDirectory;
    }

    public String getStylesheetName()
    {
        return stylesheetName;
    }

    public HeaderNavigationTemplateData getHeaderNavigation()
    {
        return headerNavigation;
    }

    public String getTitle()
    {
        return title;
    }

    public String getBodyContent()
    {
        return bodyContent;
    }

    private final String cssDirectory;
    private final String stylesheetName;
    private final HeaderNavigationTemplateData headerNavigation;
    private final String title;
    private final String bodyContent;
}
