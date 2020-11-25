package zserio.extension.doc;

/**
 * Freemarker template data context for Package emitter.
 *
 * Freemarker template data context holds all parameters used by Freemarker template data. This specialization
 * is designed for all Freemarker template data used by Package emitter.
 */
class PackageTemplateDataContext extends TemplateDataContext
{
    public PackageTemplateDataContext(DocExtensionParameters docParameters, UsedByCollector usedByCollector,
            String htmlRootDirectory, DocResourceManager docResourceManager)
    {
        super(docParameters.getWithSvgDiagrams(), usedByCollector, htmlRootDirectory);

        this.docResourceManager = docResourceManager;
    }

    public DocResourceManager getDocResourceManager()
    {
        return docResourceManager;
    }

    private final DocResourceManager docResourceManager;
}
