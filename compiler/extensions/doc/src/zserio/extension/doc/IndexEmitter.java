package zserio.extension.doc;

import java.io.File;

import zserio.ast.Package;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * HTML index emitter.
 *
 * This emitter creates main HTML index file.
 */
final class IndexEmitter
{
    public static void emit(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            Package rootPackage) throws ZserioExtensionException
    {
        final String htmlRootDirectory = ".";
        final TemplateDataContext context = new TemplateDataContext(docParameters, htmlRootDirectory);
        final SymbolTemplateData templateData = SymbolTemplateDataCreator.createData(context, rootPackage);
        final File outputFile = new File(docParameters.getOutputDir(), INDEX_FILE_NAME);
        DocFreeMarkerUtil.processTemplate(INDEX_TEMPLATE_SOURCE_NAME, templateData, outputFile);
        outputFileManager.registerOutputFile(outputFile);
    }

    private static final String INDEX_FILE_NAME = "index.html";
    private static final String INDEX_TEMPLATE_SOURCE_NAME = "index.html.ftl";
}
