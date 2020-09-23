package zserio.emit.doc;

import java.util.Set;

public class PackageOverviewTemplateData
{
    public PackageOverviewTemplateData(Set<String> packageNames)
    {
        this.packageNames = packageNames;
    }

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    private final Set<String> packageNames;
}
