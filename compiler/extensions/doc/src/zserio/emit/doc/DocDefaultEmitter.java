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
                getFileNameExtension(extensionParameters.getFileName()), outputPathName,
                HTML_CONTENT_DIRECTORY);
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

    protected static final String DOC_TEMPLATE_LOCATION = "doc/";
    protected static final String HTML_CONTENT_DIRECTORY = "content";
    protected static final String SYMBOL_COLLABORATION_DIRECTORY = "symbol_collaboration";
    protected static final String DEFAULT_PACKAGE_FILE_NAME = "[default package]";

    private final String outputPathName;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final DocResourceManager docResourceManager;
}
