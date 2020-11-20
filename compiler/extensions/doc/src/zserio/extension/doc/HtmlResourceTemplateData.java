package zserio.extension.doc;

public class HtmlResourceTemplateData
{
    public HtmlResourceTemplateData(String title, String bodyContent)
    {
        this.title = title;
        this.bodyContent = bodyContent;
    }

    public String getTitle()
    {
        return title;
    }

    public String getBodyContent()
    {
        return bodyContent;
    }

    private final String title;
    private final String bodyContent;
}
