package zserio.emit.doc;

import java.io.File;

import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;

public class PubsubEmitter extends DefaultHtmlEmitter
{
    public PubsubEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        directory = new File(directory, CONTENT_FOLDER);
        this.outputPath = outputPath;
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public void emit(PubsubType pubsubType) throws ZserioEmitException
    {
        try
        {
            ResourceManager.getInstance().setCurrentOutputDir(
                    DocEmitterTools.getDirectoryNameFromType(pubsubType));
            Template tpl = cfg.getTemplate("doc/pubsub.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(pubsubType);
            tpl.process(new PubsubTemplateData(getExpressionFormatter(), pubsubType, outputPath,
                    withSvgDiagrams), writer);
        }
        catch (Throwable exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    private final String outputPath;
    private final boolean withSvgDiagrams;
}