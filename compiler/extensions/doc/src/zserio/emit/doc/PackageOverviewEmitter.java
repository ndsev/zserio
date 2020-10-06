package zserio.emit.doc;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class PackageOverviewEmitter extends HtmlDefaultEmitter
{
    public PackageOverviewEmitter(String outputPathName, Parameters extensionParameters,
            boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);

        packageNames = new TreeSet<String>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new PackageOverviewTemplateData(packageNames);
        final File outputFile = new File(getOutputPathName(), PACKAGE_OVERVIEW_FILE_NAME);
        processHtmlTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        packageNames.add(getPackageMapper().getPackageName(pkg).toString());
    }

    private static final String PACKAGE_OVERVIEW_FILE_NAME = "package_overview.html";
    private static final String TEMPLATE_SOURCE_NAME = "package_overview.html.ftl";

    private final Set<String> packageNames;
}
