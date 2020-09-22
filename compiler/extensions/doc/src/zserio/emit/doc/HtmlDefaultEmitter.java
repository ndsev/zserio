package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class HtmlDefaultEmitter extends DocDefaultEmitter
{
    public HtmlDefaultEmitter(Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        super(extensionParameters, withSvgDiagrams, usedByCollector);
    }

    protected void processHtmlTemplate(String templateName, Object templateData, File outputFile)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputFile, false);
    }
}
