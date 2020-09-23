package zserio.emit.doc;

import java.util.Set;

public class SymbolOverviewTemplateData
{
    public SymbolOverviewTemplateData(Set<String> packageNames, Set<LinkedType> linkedTypes)
    {
        this.packageNames = packageNames;
        this.linkedTypes = linkedTypes;
    }

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    public Set<LinkedType> getLinkedTypes()
    {
        return linkedTypes;
    }

    private final Set<String> packageNames;
    private final Set<LinkedType> linkedTypes;
}
