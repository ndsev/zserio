package zserio.extension.doc;

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
