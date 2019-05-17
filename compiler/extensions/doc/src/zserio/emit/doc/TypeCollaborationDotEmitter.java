package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import zserio.ast.Root;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Emits type collaboration diagrams in DOT format per each Zserio type.
 */
public class TypeCollaborationDotEmitter extends DefaultDocEmitter
{
    /**
     * Constructor.
     *
     * @param docPath         Path to the root of generated documentation.
     * @param dotLinksPrefix  Prefix for doc links or null to use links to locally generated doc.
     * @param withSvgDiagrams True to enable dot files conversion to svg format.
     * @param dotExecutable   Dot executable to use for conversion or null to use dot exe on path.
     * @param usedByCollector Used by collector to use.
     */
    public TypeCollaborationDotEmitter(String docPath, String dotLinksPrefix, boolean withSvgDiagrams,
                                       String dotExecutable, UsedByCollector usedByCollector)
    {
        this.docPath = docPath;
        this.dotLinksPrefix = (dotLinksPrefix == null) ? "../.." : dotLinksPrefix;
        this.withSvgDiagrams = withSvgDiagrams;
        this.dotExecutable = dotExecutable;
        this.usedByCollector = usedByCollector;
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        emitDotDiagrams();
    }

    private void emitDotDiagrams() throws ZserioEmitException
    {
        for (Map.Entry<ZserioType, Set<ZserioType> > entry : usedByCollector.getUsedByTypeMap().entrySet())
        {
            final ZserioType type = entry.getKey();
            final File outputFile = DocEmitterTools.getTypeCollaborationDotFile(docPath, type);

            TypeCollaborationDotTemplateData templateData = new TypeCollaborationDotTemplateData(type,
                    usedByCollector.getUsedTypes(type), entry.getValue(), dotLinksPrefix);
            emit(outputFile, "doc/type_collaboration.dot.ftl", templateData);
            if (withSvgDiagrams)
                if (!DotFileConvertor.convertToSvg(dotExecutable, outputFile,
                                      DocEmitterTools.getTypeCollaborationSvgFile(docPath, type)))
                    throw new ZserioEmitException("Failure to convert '" + outputFile +
                            "' to SVG format!");
        }
    }

    private void emit(File outputFile, String templateFileName, Object templateData)
                 throws ZserioEmitException
    {
        try
        {
            Configuration fmConfig = new Configuration(Configuration.VERSION_2_3_28);
            fmConfig.setClassForTemplateLoading(TypeCollaborationDotEmitter.class, "/freemarker/");

            Template fmTemplate = fmConfig.getTemplate(templateFileName);

            openOutputFile(outputFile);
            fmTemplate.process(templateData, writer);
            writer.close();
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
    }

    private final String docPath;
    private final String dotLinksPrefix;
    private final boolean withSvgDiagrams;
    private final String dotExecutable;
    private final UsedByCollector usedByCollector;
}
