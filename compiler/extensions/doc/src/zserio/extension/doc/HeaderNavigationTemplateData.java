package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Package;

/**
 * FreeMarker template data for header navigation menu.
 */
public final class HeaderNavigationTemplateData
{
    public HeaderNavigationTemplateData(TemplateDataContext context, Package rootPackage,
            boolean hasSchemaRules, ActiveItem activeItem)
    {
        final SymbolTemplateData rootPackageSymbol = SymbolTemplateDataCreator.createData(context, rootPackage);
        navigationItems.add(new NavigationItem(
                "Packages", rootPackageSymbol.getHtmlLink().getHtmlPage()));
        if (hasSchemaRules)
        {
            navigationItems.add(new NavigationItem(
                    "Rules", RulesOverviewEmitter.getRulesOverviewHtmlLink(context.getContentDirectory())));
        }

        this.activeItem = activeItem;
    }

    public Iterable<NavigationItem> getNavigationItems()
    {
        return navigationItems;
    }

    public int getActiveItemIndex()
    {
        return activeItem.getValue();
    }

    public static final class NavigationItem
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

    enum ActiveItem
    {
        NO_ITEM(-1),
        PACKAGES_ITEM(0),
        RULES_ITEM(1);

        public int getValue()
        {
            return index;
        }

        private ActiveItem(int index)
        {
            this.index = index;
        }

        private final int index;
    };

    private final List<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
    private final ActiveItem activeItem;
};
