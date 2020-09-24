package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;

/**
 * Emits CSS styles file.
 */
class WebStylesEmitter
{
    static void emit(String outputPathName) throws ZserioEmitException
    {
        processTemplate(WEB_STYLES_TEMPLATE_SOURCE_NAME, new File(outputPathName, WEB_STYLES_FILE_NAME));
    }

    static void processTemplate(String templateName, File outputFile) throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DotDefaultEmitter.DOC_TEMPLATE_LOCATION + templateName, null, outputFile,
                false);
    }

    private static final String WEB_STYLES_FILE_NAME = "webStyles.css"; // TODO[mikir] to rename
    private static final String WEB_STYLES_TEMPLATE_SOURCE_NAME = "web_styles.css.ftl";
}
