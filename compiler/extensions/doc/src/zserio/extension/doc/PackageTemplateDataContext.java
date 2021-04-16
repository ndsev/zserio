package zserio.extension.doc;

/**
 * Freemarker template data context for Package emitter.
 *
 * Freemarker template data context holds all parameters used by Freemarker template data. This specialization
 * is designed for all Freemarker template data used by Package emitter.
 */
class PackageTemplateDataContext extends ContentTemplateDataContext
{
    public PackageTemplateDataContext(DocExtensionParameters docParameters, String htmlRootDirectory,
            UsedByCollector usedByCollector, UsedByChoiceCollector usedByChoiceCollector,
            DocResourceManager docResourceManager)
    {
        super(docParameters, htmlRootDirectory, docResourceManager);

        this.usedByCollector = usedByCollector;
        this.usedByChoiceCollector = usedByChoiceCollector;
    }

    public UsedByCollector getUsedByCollector()
    {
        return usedByCollector;
    }

    public UsedByChoiceCollector getUsedByChoiceCollector()
    {
        return usedByChoiceCollector;
    }

    private final UsedByCollector usedByCollector;
    private final UsedByChoiceCollector usedByChoiceCollector;
}
