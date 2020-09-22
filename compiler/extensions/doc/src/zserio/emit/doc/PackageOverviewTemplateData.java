package zserio.emit.doc;

import java.util.Set;

public class PackageOverviewTemplateData
{
    public PackageOverviewTemplateData(Set<String> packageList)
    {
        this.packageList = packageList;
    }

    public Set<String> getPackageList()
    {
        return packageList;
    }

    private final Set<String> packageList;
}
