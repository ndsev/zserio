package zserio.emit.doc;

import java.util.List;

public class SymbolTemplateData implements Comparable<SymbolTemplateData>
{
    public SymbolTemplateData(String name, String htmlClass, String htmlTitle, String htmlLinkPage,
            String htmlLinkAnchor, List<SymbolTemplateData> templateArguments)
    {
        this(name, htmlClass, htmlTitle, new HtmlLink(htmlLinkPage, htmlLinkAnchor), templateArguments);
    }

    public SymbolTemplateData(String name, String htmlClass, String htmlTitle,
            List<SymbolTemplateData> templateArguments)
    {
        this(name, htmlClass, htmlTitle, null, templateArguments);
    }

    public SymbolTemplateData(String name, String htmlClass, String htmlTitle, HtmlLink htmlLink,
            List<SymbolTemplateData> templateArguments)
    {
        this.name = name;
        this.htmlClass = htmlClass;
        this.htmlTitle = htmlTitle;
        this.htmlLink = htmlLink;
        this.templateArguments = templateArguments;
    }

    public SymbolTemplateData(String alias, SymbolTemplateData other)
    {
        this.name = alias;
        this.htmlClass = other.htmlClass;
        this.htmlTitle = other.htmlTitle;
        this.htmlLink = other.htmlLink;
        this.templateArguments = other.templateArguments;
    }

    @Override
    public int compareTo(SymbolTemplateData other)
    {
        int result = name.compareTo(other.name);
        if (result == 0 && htmlLink != null && other.htmlLink != null)
            result = htmlLink.getHtmlPage().compareTo(other.htmlLink.getHtmlPage());

        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if ( !(other instanceof SymbolTemplateData) )
            return false;

        return (this == other) || compareTo((SymbolTemplateData)other) == 0;
    }

    @Override
    public int hashCode()
    {
        String hashString = name;
        if (htmlLink != null)
            hashString += htmlLink.getHtmlPage();

        return hashString.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public String getHtmlClass()
    {
        return htmlClass;
    }

    public String getHtmlTitle()
    {
        return htmlTitle;
    }

    public HtmlLink getHtmlLink()
    {
        return htmlLink;
    }

    public Iterable<SymbolTemplateData> getTemplateArguments()
    {
        return templateArguments;
    }

    public static class HtmlLink
    {
        public HtmlLink(String htmlPage, String htmlAnchor)
        {
            this.htmlPage = htmlPage;
            this.htmlAnchor = htmlAnchor;
        }

        public String getHtmlPage()
        {
            return htmlPage;
        }

        public String getHtmlAnchor()
        {
            return htmlAnchor;
        }

        private final String htmlPage;
        private final String htmlAnchor;
    }

    private final String name;
    private final String htmlClass;
    private final String htmlTitle;
    private final HtmlLink htmlLink;
    private final List<SymbolTemplateData> templateArguments;
}
