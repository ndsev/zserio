package zserio.extension.doc;

import java.io.File;

import zserio.ast.Package;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

/**
 * Emits index HTML file.
 */
class IndexEmitter
{
    public static void emit(String outputPathName, ExtensionParameters extensionParameters,
            boolean withSvgDiagrams, UsedByCollector usedByCollector, PackageCollector packageCollector,
            Package rootPackage) throws ZserioExtensionException
    {
        final String htmlRootDirectory = ".";
        final TemplateDataContext context = new TemplateDataContext(outputPathName, extensionParameters,
                withSvgDiagrams, usedByCollector, packageCollector, htmlRootDirectory);
        final SymbolTemplateData templateData = SymbolTemplateDataCreator.createData(context, rootPackage);
        final File outputFile = new File(outputPathName, INDEX_FILE_NAME);
        DocFreeMarkerUtil.processTemplate(INDEX_TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    private static final String INDEX_FILE_NAME = "index.html";
    private static final String INDEX_TEMPLATE_SOURCE_NAME = "index.html.ftl";
}
