package zserio.emit.doc;

import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

class DocDefaultEmitter extends DefaultEmitter
{
    public DocDefaultEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        this.outputPathName = outputPathName;
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;

        final Iterable<String> topLevelPackageNameList = extensionParameters.getTopLevelPackageNameList();
        packageMapper = new PackageMapper(topLevelPackageNameList);

        resourceManager = new ResourceManager(extensionParameters.getPathName(),
                getFileNameExtension(extensionParameters.getFileName()), outputPathName, HTML_CONTENT_DIRECTORY);
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

    protected PackageMapper getPackageMapper()
    {
        return packageMapper;
    }

    protected ResourceManager getResourceManager()
    {
        return resourceManager;
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
    protected static final String DB_STRUCTURE_DIRECTORY = "db_structure";
    protected static final String DEFAULT_PACKAGE_FILE_NAME = "[default package]";

    private final String outputPathName;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final PackageMapper packageMapper;
    private final ResourceManager resourceManager;
}
