package zserio.emit.doc;

import java.io.File;
import java.io.Writer;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class HtmlDefaultEmitter extends DocDefaultEmitter
{
    public HtmlDefaultEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);
    }

    protected void processHtmlTemplate(String templateName, Object templateData, File outputFile)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputFile, false);
    }

    protected void processHtmlTemplate(String templateName, Object templateData, Writer outputWriter)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputWriter);
    }

    protected static final String HTML_FILE_EXTENSION = ".html";
}
