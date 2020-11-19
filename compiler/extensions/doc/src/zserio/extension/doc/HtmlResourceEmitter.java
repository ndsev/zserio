package zserio.extension.doc;

import java.io.File;

import zserio.extension.common.ZserioExtensionException;

/**
 * Emits an HTML resource.
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

    public static class HtmlResourceTemplateData
    {
        public HtmlResourceTemplateData(String title, String bodyContent)
        {
            this.title = title;
            this.bodyContent = bodyContent;
        }

        public String getTitle()
        {
            return title;
        }

        public String getBodyContent()
        {
            return bodyContent;
        }

        private final String title;
        private final String bodyContent;
    }

    private static final String TEMPLATE_SOURCE_NAME = "html_resource.html.ftl";
}
