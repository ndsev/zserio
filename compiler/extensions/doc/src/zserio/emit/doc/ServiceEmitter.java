package zserio.emit.doc;

import java.io.File;

import zserio.ast.ServiceType;
import zserio.ast.ZserioException;
import freemarker.template.Template;

public class ServiceEmitter extends DefaultHtmlEmitter
{
    public ServiceEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        directory = new File(directory, CONTENT_FOLDER);
        this.outputPath = outputPath;
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public void emit(ServiceType serviceType)
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/service.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(serviceType);
            tpl.process(new ServiceTemplateData(serviceType, outputPath, withSvgDiagrams), writer);
        }
        catch (Throwable e)
        {
            throw new ZserioException(e);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    private String outputPath;
    private boolean withSvgDiagrams;
}