package zserio.emit.doc;

import java.io.File;
import java.util.TreeSet;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class PackageOverviewEmitter extends HtmlDefaultEmitter
{
    public PackageOverviewEmitter(String outputPathName, Parameters extensionParameters,
            boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(extensionParameters, withSvgDiagrams, usedByCollector);

        this.outputPathName = outputPathName;
        packages = new TreeSet<String>();
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        super.beginPackage(pkg);

        packages.add(pkg.getPackageName().toString()); // TODO[mikir] replace it when top level package is ready
//        packages.add(getPackageMapper().getPackageName(pkg).toString());
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new PackageOverviewTemplateData(packages);
        final File outputFile = new File(outputPathName, PACKAGE_OVERVIEW_FILE_NAME);
        processHtmlTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    private static final String PACKAGE_OVERVIEW_FILE_NAME = "package_overview.html";
    private static final String TEMPLATE_SOURCE_NAME = "package_overview.html.ftl";

    private final String outputPathName;
    private final TreeSet<String> packages;
}
