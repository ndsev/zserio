package zserio.emit.doc;

import java.io.File;

import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class DotDefaultEmitter extends DocDefaultEmitter
{
    public DotDefaultEmitter(String outputPathName, Parameters extensionParameters, String dotLinksPrefix,
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);

        this.dotLinksPrefix = (dotLinksPrefix == null) ? ".." : dotLinksPrefix;
        this.dotExecutable = dotExecutable;
    }

    protected String getDotLinksPrefix()
    {
        return dotLinksPrefix;
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

    private final String dotLinksPrefix;
    private final String dotExecutable;
}
