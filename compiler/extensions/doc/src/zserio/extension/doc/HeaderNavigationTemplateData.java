package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Package;

/**
 * FreeMarker template data for header navigation menu.
 */
public class HeaderNavigationTemplateData
{
    public HeaderNavigationTemplateData(ContentTemplateDataContext context, Package rootPackage,
            boolean hasSchemaRules, int activeItem)
    {
        final SymbolTemplateData rootPackageSymbol = SymbolTemplateDataCreator.createData(context, rootPackage);
        navigationItems.add(new NavigationItem(
                "Packages", rootPackageSymbol.getHtmlLink().getHtmlPage()));
        navigationItems.add(new NavigationItem(
                "Rules", RulesOverviewEmitter.getRulesOverviewHtmlLink(context.getContentDirectory())));

        this.activeItem = activeItem;
    }

    public Iterable<NavigationItem> getNavigationItems()
    {
        return navigationItems;
    }

    public int getActiveItem()
    {
        return activeItem;
    }

    public static class NavigationItem
    {
        public NavigationItem(String name, String htmlLink)
        {
            this.name = name;
            this.htmlLink = htmlLink;
        }

        public String getName()
        {
            return name;
        }

        public String getHtmlLink()
        {
            return htmlLink;
        }

        private final String name;
        private final String htmlLink;
    }

    private final List<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
    private final int activeItem;

    static final int NO_ITEM = -1;
    static final int PACKAGES_ITEM = 0;
    static final int RULES_ITEM = 1;
};
