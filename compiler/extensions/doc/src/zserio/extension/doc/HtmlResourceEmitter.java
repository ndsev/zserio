package zserio.extension.doc;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import zserio.ast.Package;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * HTML resource emitter.
 *
 * HTML resource emitter creates valid stand-alone HTML file from the converted markdown documentation comment
 * to HTML (which does not contain valid header or body).
 */
final class HtmlResourceEmitter
{
    public HtmlResourceEmitter(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            Package rootPackage, boolean hasSchemaRules)
    {
        this.outputFileManager = outputFileManager;
        this.htmlRootDirectory = Paths.get(docParameters.getOutputDir()).toAbsolutePath();
        this.docParameters = docParameters;
        this.rootPackage = rootPackage;
        this.hasSchemaRules = hasSchemaRules;
    }

    public void emit(Path outputDir, String fileName, String title, String bodyContent)
            throws ZserioExtensionException
    {
        final TemplateDataContext context = new TemplateDataContext(docParameters,
                outputDir.relativize(htmlRootDirectory).toString());
        final HtmlResourceTemplateData templateData = new HtmlResourceTemplateData(
                context, rootPackage, hasSchemaRules, title, bodyContent);

        final File outputFile = new File(outputDir.toString(), fileName);
        DocFreeMarkerUtil.processTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
        outputFileManager.registerOutputFile(outputFile);
    }

    private final OutputFileManager outputFileManager;
    private final Path htmlRootDirectory;
    private final DocExtensionParameters docParameters;
    private final Package rootPackage;
    private final boolean hasSchemaRules;

    private static final String TEMPLATE_SOURCE_NAME = "html_resource.html.ftl";
}
