package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * FreeMarker template data for symbol used by Package emitter.
 *
 * Symbol holds all information needed to show correct hyperlink to the symbol.
 */
public final class SymbolTemplateData implements Comparable<SymbolTemplateData>
{
    public SymbolTemplateData(String name, String typeName, String htmlTitle, String htmlLinkPage,
            String htmlLinkAnchor)
    {
        this(name, typeName, htmlTitle, new HtmlLink(htmlLinkPage, htmlLinkAnchor),
                new ArrayList<SymbolTemplateData>());
    }

    public SymbolTemplateData(String name, String typeName, String htmlTitle, String htmlLinkPage,
            String htmlLinkAnchor, List<SymbolTemplateData> templateArguments)
    {
        this(name, typeName, htmlTitle, new HtmlLink(htmlLinkPage, htmlLinkAnchor), templateArguments);
    }

    public SymbolTemplateData(String name, String typeName, String htmlTitle)
    {
        this(name, typeName, htmlTitle, null, new ArrayList<SymbolTemplateData>());
    }

    public SymbolTemplateData(String name, String typeName, String htmlTitle,
            List<SymbolTemplateData> templateArguments)
    {
        this(name, typeName, htmlTitle, null, templateArguments);
    }

    public SymbolTemplateData(String name, String typeName, String htmlTitle, HtmlLink htmlLink,
            List<SymbolTemplateData> templateArguments)
    {
        this.name = name;
        this.typeName = typeName;
        this.htmlTitle = htmlTitle;
        this.htmlLink = htmlLink;
        this.templateArguments = templateArguments;
    }

    @Override
    public int compareTo(SymbolTemplateData other)
    {
        int result = name.compareTo(other.name);
        if (result == 0)
            result = typeName.compareTo(other.typeName);
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
        String hashString = name + typeName;
        if (htmlLink != null)
            hashString += htmlLink.getHtmlPage();

        return hashString.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public String getTypeName()
    {
        return typeName;
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

    public static final class HtmlLink
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
    private final String typeName;
    private final String htmlTitle;
    private final HtmlLink htmlLink;
    private final List<SymbolTemplateData> templateArguments;
}
