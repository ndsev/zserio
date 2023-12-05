package zserio.extension.doc;

import java.io.File;
import java.io.Writer;

import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.ZserioExtensionException;

/**
 * FreeMarker utility methods for documentation extension.
 *
 * The class provides location of FreeMarker templates for all documentation emitters.
 */
final class DocFreeMarkerUtil
{
    public static void processTemplate(String templateName, Object templateData, File outputFile)
            throws ZserioExtensionException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputFile);
    }

    public static void processTemplate(String templateName, Object templateData, Writer outputWriter)
            throws ZserioExtensionException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputWriter);
    }

    public static final String DOC_TEMPLATE_LOCATION = "doc/";
}
