package zserio.emit.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getResourceManager(),
                HTML_CONTENT_DIRECTORY, SYMBOL_COLLABORATION_DIRECTORY);

        packages = new ArrayList<Package>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new PackageOverviewTemplateData(context, packages);
        final File outputFile = new File(getOutputPathName(), PACKAGE_OVERVIEW_FILE_NAME);
        processHtmlTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        packages.add(pkg);
    }

    private static final String PACKAGE_OVERVIEW_FILE_NAME = "package_overview.html";
    private static final String TEMPLATE_SOURCE_NAME = "package_overview.html.ftl";

    private final TemplateDataContext context;
    private final List<Package> packages;
}
