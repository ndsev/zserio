package zserio.emit.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The functions which handles FreeMarker Java Template Engine.
 */
public class FreeMarkerUtil
{
    /**
     * Processes FreeMarker template with the provided data model and generates output.
     *
     * @param templateName      The template name with the path relatively to "/freemarker" directory.
     * @param templateDataModel The template data model to apply.
     * @param outputWriter      The writer to use for generated output.
     *
     * @throws ZserioEmitException In case of any template error.
     */
    public static void processTemplate(String templateName, Object templateDataModel, Writer outputWriter)
            throws ZserioEmitException
    {
        if (freeMarkerConfig == null)
        {
            final Configuration newFreeMarkerConfig = new Configuration(Configuration.VERSION_2_3_28);
            newFreeMarkerConfig.setClassForTemplateLoading(FreeMarkerUtil.class, "/freemarker/");
            freeMarkerConfig = newFreeMarkerConfig;
            amalgamatedDirectories = new HashSet<String>();
        }

        try
        {
            final Template freeMarkerTemplate = freeMarkerConfig.getTemplate(templateName);
            freeMarkerTemplate.process(templateDataModel, outputWriter);
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

    /**
     * Processes FreeMarker template with the provided data model and generates output.
     *
     * @param templateName      The template name with the path relatively to "/freemarker" directory.
     * @param templateDataModel The template data model to apply.
     * @param outputFile        The output to be generated.
     * @param amalgamate        True if the generated output will be amalgamated to the output file.
     *
     * @throws ZserioEmitException In case of any template error.
     */
    public static void processTemplate(String templateName, Object templateDataModel, File outputFile,
            boolean amalgamate) throws ZserioEmitException
    {
        FileUtil.createOutputDirectory(outputFile);

        boolean append = false;
        if (amalgamate)
        {
            final String outputDirName = outputFile.getParent();
            if (amalgamatedDirectories.contains(outputDirName))
                append = true;
            else
                amalgamatedDirectories.add(outputDirName);
        }

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try
        {
            fileOutputStream = new FileOutputStream(outputFile, append);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            if (append)
                bufferedWriter.newLine();
            processTemplate(templateName, templateDataModel, bufferedWriter);
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            try
            {
                if (bufferedWriter != null)
                    bufferedWriter.close();
                else if (outputStreamWriter != null)
                    outputStreamWriter.close();
                else if (fileOutputStream != null)
                    fileOutputStream.close();
            }
            catch( IOException e )
            {
            }
        }
    }

    private static volatile Configuration freeMarkerConfig;
    private static Set<String> amalgamatedDirectories;
}
