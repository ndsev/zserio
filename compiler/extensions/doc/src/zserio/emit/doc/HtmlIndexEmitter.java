package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;

/**
 * Emits index HTML file.
 */
public class HtmlIndexEmitter
{
    static void emit(String outputPathName) throws ZserioEmitException
    {
        processTemplate(HTML_INDEX_TEMPLATE_SOURCE_NAME, new File(outputPathName, HTML_INDEX_FILE_NAME));
    }

    static void processTemplate(String templateName, File outputFile) throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DotDefaultEmitter.DOC_TEMPLATE_LOCATION + templateName, null, outputFile,
                false);
    }

    private static final String HTML_INDEX_FILE_NAME = "index.html";
    private static final String HTML_INDEX_TEMPLATE_SOURCE_NAME = "index.html.ftl";
}
