package zserio.extension.doc;

import java.io.File;

import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Style sheet emitter.
 *
 * The style sheet emitter creates CSS styles file.
 */
final class StylesheetEmitter
{
    static void emit(OutputFileManager outputFileManager, DocExtensionParameters docParameters)
            throws ZserioExtensionException
    {
        final File outputDirectory = new File(docParameters.getOutputDir(), DocDirectories.CSS_DIRECTORY);
        final File outputFile = new File(outputDirectory, STYLESHEET_FILE_NAME);
        DocFreeMarkerUtil.processTemplate(STYLESHEET_TEMPLATE_SOURCE_NAME, null, outputFile);
        outputFileManager.registerOutputFile(outputFile);
    }

    static final String STYLESHEET_FILE_NAME = "stylesheet.css";

    private static final String STYLESHEET_TEMPLATE_SOURCE_NAME = "stylesheet.css.ftl";
}
