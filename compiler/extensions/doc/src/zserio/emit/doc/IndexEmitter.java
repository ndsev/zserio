package zserio.emit.doc;

import java.io.File;

import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

/**
 * Emits index HTML file.
 */
class IndexEmitter
{
    public static void emit(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, PackageCollector packageCollector, Package rootPackage)
                    throws ZserioEmitException
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
