package zserio.extension.doc;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.Package;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker template data for rules overview emitter.
 */
public final class RulesOverviewTemplateData
{
    public RulesOverviewTemplateData(ContentTemplateDataContext context, Package rootPackage,
            Set<PackageRulesTemplateData> packagesRuleGroups) throws ZserioExtensionException
    {
        cssDirectory = context.getCssDirectory();
        jsDirectory = context.getJsDirectory();
        stylesheetName = StylesheetEmitter.STYLESHEET_FILE_NAME;
        final boolean hasSchemaRules = true; // this is the rules view!
        headerNavigation = new HeaderNavigationTemplateData(
                context, rootPackage, hasSchemaRules, HeaderNavigationTemplateData.ActiveItem.RULES_ITEM);

        this.packagesRuleGroups = packagesRuleGroups;
    }

    public String getCssDirectory()
    {
        return cssDirectory;
    }

    public String getJsDirectory()
    {
        return jsDirectory;
    }

    public String getStylesheetName()
    {
        return stylesheetName;
    }

    public HeaderNavigationTemplateData getHeaderNavigation()
    {
        return headerNavigation;
    }

    public Iterable<PackageRulesTemplateData> getPackagesRuleGroups()
    {
        return packagesRuleGroups;
    }

    public static final class PackageRulesTemplateData implements Comparable<PackageRulesTemplateData>
    {
        public PackageRulesTemplateData(ContentTemplateDataContext context, Package pkg,
                List<RuleGroupTemplateData> ruleGroups) throws ZserioExtensionException
        {
            packageSymbol = SymbolTemplateDataCreator.createData(context, pkg);
            this.ruleGroups.addAll(ruleGroups);
        }

        @Override
        public int compareTo(PackageRulesTemplateData other)
        {
            return packageSymbol.compareTo(other.packageSymbol);
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof PackageRulesTemplateData))
                return false;

            return (this == other) || compareTo((PackageRulesTemplateData)other) == 0;
        }

        @Override
        public int hashCode()
        {
            return packageSymbol.hashCode();
        }

        public SymbolTemplateData getPackageSymbol()
        {
            return packageSymbol;
        }

        public Iterable<RuleGroupTemplateData> getRuleGroups()
        {
            return ruleGroups;
        }

        private static class RuleGroupTemplateDataComparator
                implements Comparator<RuleGroupTemplateData>, Serializable
        {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(RuleGroupTemplateData lhs, RuleGroupTemplateData rhs)
            {
                return lhs.getSymbol().compareTo(rhs.getSymbol());
            }
        }

        private final SymbolTemplateData packageSymbol;
        // we want to have sorted rule groups in the overview
        private final Set<RuleGroupTemplateData> ruleGroups =
                new TreeSet<RuleGroupTemplateData>(new RuleGroupTemplateDataComparator());
    }

    private final String cssDirectory;
    private final String jsDirectory;
    private final String stylesheetName;
    private final HeaderNavigationTemplateData headerNavigation;
    private final Set<PackageRulesTemplateData> packagesRuleGroups;
}