package zserio.extension.doc;

/**
 * Freemarker template data context for content emitters.
 *
 * Freemarker template data context holds all parameters used by Freemarker template data. This specialization
 * is designed for all Freemarker template data used by content emitters.
 */
class ContentTemplateDataContext extends TemplateDataContext
{
    public ContentTemplateDataContext(DocExtensionParameters docParameters, String htmlRootDirectory,
            DocResourceManager docResourceManager)
    {
        super(docParameters, htmlRootDirectory);

        this.docResourceManager = docResourceManager;
    }

    public DocResourceManager getDocResourceManager()
    {
        return docResourceManager;
    }

    private final DocResourceManager docResourceManager;
}
