package zserio.emit.doc;

import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

class DocDefaultEmitter extends DefaultEmitter
{
    public DocDefaultEmitter(Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;

        final Iterable<String> topLevelPackageNameList = extensionParameters.getTopLevelPackageNameList();
        packageMapper = new PackageMapper(topLevelPackageNameList);
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

    protected static final String DOC_TEMPLATE_LOCATION = "doc/";
    protected static final String SYMBOL_COLLABORATION_DIRECTORY = "symbol_collaboration";
    protected static final String HTML_CONTENT_DIRECTORY = "content";

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final PackageMapper packageMapper;
}
