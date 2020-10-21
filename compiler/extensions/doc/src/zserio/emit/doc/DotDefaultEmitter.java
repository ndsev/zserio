package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class DotDefaultEmitter extends DocDefaultEmitter
{
    public DotDefaultEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            String dotExecutable, UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);

        this.dotExecutable = dotExecutable;
    }

    protected String getDotExecutable()
    {
        return dotExecutable;
    }

    protected void processDotTemplate(String templateName, Object templateData, File outputDotFile,
            File outputSvgFile) throws ZserioEmitException
    {
        FreeMarkerUtil.processTemplate(DOC_TEMPLATE_LOCATION + templateName, templateData, outputDotFile,
                false);
        if (getWithSvgDiagrams())
        {
            if (!DotToSvgConverter.convert(dotExecutable, outputDotFile, outputSvgFile))
                throw new ZserioEmitException("Failure to convert '" + outputDotFile + "' to SVG format!");
        }

    }

    protected static final String DOT_FILE_EXTENSION = ".dot";
    protected static final String SVG_FILE_EXTENSION = ".svg";

    private final String dotExecutable;
}
