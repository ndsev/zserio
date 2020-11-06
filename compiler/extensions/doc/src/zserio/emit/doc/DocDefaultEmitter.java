package zserio.emit.doc;

import zserio.emit.common.DefaultTreeWalker;
import zserio.tools.Parameters;

abstract class DocDefaultEmitter extends DefaultTreeWalker
{
    public DocDefaultEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        this.outputPathName = outputPathName;
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;

        docResourceManager = new DocResourceManager(extensionParameters.getPathName(),
                getFileNameExtension(extensionParameters.getFileName()), outputPathName, CONTENT_DIRECTORY);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    protected String getOutputPathName()
    {
        return outputPathName;
    }

    protected boolean getWithSvgDiagrams()
    {
        return withSvgDiagrams;
    }

    protected UsedByCollector getUsedByCollector()
    {
        return usedByCollector;
    }

    protected DocResourceManager getResourceManager()
    {
        return docResourceManager;
    }

    // TODO[Mi-L@]: Provide by core?!
    private String getFileNameExtension(String fileName)
    {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0)
            return fileName.substring(lastDotIndex);

        return "";
    }

    static final String CONTENT_DIRECTORY = "content";
    static final String CSS_DIRECTORY = "css";
    static final String JS_DIRECTORY = "js";
    static final String RESOURCES_DIRECTORY = "resources";
    static final String SYMBOL_COLLABORATION_DIRECTORY = "diagrams";

    protected static final String DOC_TEMPLATE_LOCATION = "doc/";
    protected static final String DEFAULT_PACKAGE_FILE_NAME = "[default package]";

    private final String outputPathName;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final DocResourceManager docResourceManager;
}
