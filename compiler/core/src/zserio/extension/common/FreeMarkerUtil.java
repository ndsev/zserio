package zserio.extension.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
     * @param templateName      The template name with the path relatively to "/FREEMARKER_LOCATION" directory.
     * @param templateDataModel The template data model to apply.
     * @param outputWriter      The writer to use for generated output.
     *
     * @throws ZserioExtensionException In case of any template error.
     */
    public static void processTemplate(String templateName, Object templateDataModel, Writer outputWriter)
            throws ZserioExtensionException
    {
        if (freeMarkerConfig == null)
        {
            final Configuration newFreeMarkerConfig = new Configuration(Configuration.VERSION_2_3_28);
            newFreeMarkerConfig.setClassForTemplateLoading(FreeMarkerUtil.class, '/' + FREEMARKER_LOCATION);
            newFreeMarkerConfig.setOutputEncoding("UTF-8");

            freeMarkerConfig = newFreeMarkerConfig;
        }

        try
        {
            final Template freeMarkerTemplate = freeMarkerConfig.getTemplate(templateName);
            freeMarkerTemplate.process(templateDataModel, outputWriter);
        }
        catch (IOException exception)
        {
            throw new ZserioExtensionException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioExtensionException(exception.getMessage());
        }
    }

    /**
     * Processes FreeMarker template with the provided data model and generates output.
     *
     * @param templateName      The template name with the path relatively to "/FREEMARKER_LOCATION" directory.
     * @param templateDataModel The template data model to apply.
     * @param outputFile        The output to be generated.
     *
     * @throws ZserioExtensionException In case of any template error.
     */
    public static void processTemplate(String templateName, Object templateDataModel, File outputFile)
            throws ZserioExtensionException
    {
        processTemplate(templateName, templateDataModel, outputFile, false);
    }

    /**
     * Processes FreeMarker template with the provided data model and generates output.
     *
     * @param templateName      The template name with the path relatively to "/FREEMARKER_LOCATION" directory.
     * @param templateDataModel The template data model to apply.
     * @param outputFile        The output to be generated.
     * @param amalgamate        True if the generated output will be amalgamated to the output file.
     *
     * @throws ZserioExtensionException In case of any template error.
     */
    public static void processTemplate(String templateName, Object templateDataModel, File outputFile,
            boolean amalgamate) throws ZserioExtensionException
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
            throw new ZserioExtensionException(exception.getMessage());
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

    /**
     * Gets all lines of the requested FreeMarker template.
     *
     * @param templateName Name of the FreeMarker template to read.
     *
     * @return List of lines read from FreeMarker template.
     *
     * @throws ZserioExtensionException When the template is not available.
     */
    public static List<String> readFreemarkerTemplate(String templateName) throws ZserioExtensionException
    {
        final String fullTemplateName = FREEMARKER_LOCATION + templateName;
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(getFreemarkerTemplateStream(fullTemplateName), "UTF-8")))
        {
            final List<String> lines = new ArrayList<String>();
            while (reader.ready())
                lines.add(reader.readLine());
            return lines;
        }
        catch (IOException e)
        {
            throw new ZserioExtensionException("Failed to read template '" +
                    fullTemplateName + "': " + e.toString());
        }
    }

    private static InputStream getFreemarkerTemplateStream(String templateName) throws ZserioExtensionException
    {
        InputStream resourceStream = null;
        try
        {
            final ClassLoader classLoader = FreeMarkerUtil.class.getClassLoader();
            resourceStream = classLoader.getResourceAsStream(templateName);
        }
        catch (Exception e)
        {
            throw new ZserioExtensionException("Failed to get resource file for template '" +
                    templateName + "':" + e.getMessage());
        }

        if (resourceStream == null)
        {
            throw new ZserioExtensionException("Failed to get resource file for template '" +
                    templateName + "'!");
        }

        return resourceStream;
    }

    private static volatile Configuration freeMarkerConfig;
    private static Set<String> amalgamatedDirectories = new HashSet<String>();

    private static final String FREEMARKER_LOCATION = "freemarker/";
}
