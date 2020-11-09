package zserio.emit.doc;

import java.io.File;
import java.io.Writer;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;

abstract class HtmlDefaultEmitter extends DocDefaultEmitter
{
    public HtmlDefaultEmitter()
    {
        super();
    }

    protected static void processHtmlTemplate(String templateName, Object templateData, File outputFile)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputFile, false);
    }

    protected static void processHtmlTemplate(String templateName, Object templateData, Writer outputWriter)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputWriter);
    }

    protected static final String HTML_FILE_EXTENSION = ".html";
}
