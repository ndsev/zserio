package zserio.emit.doc;

import java.io.File;
import java.io.Writer;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;

class DocFreeMarkerUtil
{
    public static void processTemplate(String templateName, Object templateData, File outputFile)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputFile);
    }

    public static void processTemplate(String templateName, Object templateData, Writer outputWriter)
            throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputWriter);
    }

    public static final String DOC_TEMPLATE_LOCATION = "doc/";
}
