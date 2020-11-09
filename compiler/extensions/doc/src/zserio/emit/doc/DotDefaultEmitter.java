package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;

abstract class DotDefaultEmitter extends DocDefaultEmitter
{
    public DotDefaultEmitter(boolean withSvgDiagrams, String dotExecutable)
    {
        super();

        this.withSvgDiagrams = withSvgDiagrams;
        this.dotExecutable = dotExecutable;
    }

    protected void processDotTemplate(String templateName, Object templateData, File outputDotFile,
            File outputSvgFile) throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputDotFile,
                false);
        if (withSvgDiagrams)
        {
            if (!DotToSvgConverter.convert(dotExecutable, outputDotFile, outputSvgFile))
                throw new ZserioEmitException("Failure to convert '" + outputDotFile + "' to SVG format!");
        }

    }

    protected static final String DOT_FILE_EXTENSION = ".dot";
    protected static final String SVG_FILE_EXTENSION = ".svg";

    private final boolean withSvgDiagrams;
    private final String dotExecutable;
}
