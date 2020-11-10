package zserio.emit.doc;

import java.util.List;

public class SymbolTemplateData implements Comparable<SymbolTemplateData>
{
    public SymbolTemplateData(String name, String htmlTitle, String htmlLinkPage,
            String htmlLinkAnchor, List<SymbolTemplateData> templateArguments)
    {
        this(name, htmlTitle, new HtmlLink(htmlLinkPage, htmlLinkAnchor), templateArguments);
    }

    public SymbolTemplateData(String name, String htmlTitle, List<SymbolTemplateData> templateArguments)
    {
        this(name, htmlTitle, null, templateArguments);
    }

    public SymbolTemplateData(String name, String htmlTitle, HtmlLink htmlLink,
            List<SymbolTemplateData> templateArguments)
    {
        this.name = name;
        this.htmlTitle = htmlTitle;
        this.htmlLink = htmlLink;
        this.templateArguments = templateArguments;
    }

    public SymbolTemplateData(String alias, SymbolTemplateData other)
    {
        this.name = alias;
        this.htmlTitle = other.htmlTitle;
        this.htmlLink = other.htmlLink;
        this.templateArguments = other.templateArguments;
    }

    @Override
    public int compareTo(SymbolTemplateData other)
    {
        int result = name.compareTo(other.name);
        if (result == 0)
            result = compareTemplateArguments(other);
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

    private int compareTemplateArguments(SymbolTemplateData other)
    {
        int result = 0;
        int minSize = Math.min(templateArguments.size(), other.templateArguments.size());
        for (int i = 0; i < minSize && result == 0; ++i)
            result = templateArguments.get(i).compareTo(other.templateArguments.get(i));
        if (result == 0)
            result = templateArguments.size() - other.templateArguments.size();
        return result;
    }

    private final String name;
    private final String htmlTitle;
    private final HtmlLink htmlLink;
    private final List<SymbolTemplateData> templateArguments;
}
