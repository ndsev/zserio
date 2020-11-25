package zserio.extension.doc;

import java.io.File;

import zserio.extension.common.ZserioExtensionException;

/**
 * HTML resource emitter.
 *
 * HTML resource emitter creates valid standalone HTML file from the converted markdown documentation comment
 * to HTML (which does not contain valid header or body).
 */
class HtmlResourceEmitter
{
    public static void emit(String outputDir, String fileName, String title, String bodyContent)
            throws ZserioExtensionException
    {
        final HtmlResourceTemplateData templateData = new HtmlResourceTemplateData(title, bodyContent);

        final File outputFile = new File(outputDir, fileName);
        DocFreeMarkerUtil.processTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    private static final String TEMPLATE_SOURCE_NAME = "html_resource.html.ftl";
}
