package zserio.emit.doc;

import zserio.ast.Package;
import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class DocDefaultEmitter extends DefaultEmitter
{
    public DocDefaultEmitter(Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;

        topLevelPackageNameList = extensionParameters.getTopLevelPackageNameList();
        packageMapper = null;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        if (packageMapper == null)
            packageMapper = new PackageMapper(pkg, topLevelPackageNameList);
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

    public static final String DOC_TEMPLATE_LOCATION = "doc/";

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    private final Iterable<String> topLevelPackageNameList;
    private PackageMapper packageMapper;
}
