package zserio.extension.python;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

abstract class PythonDefaultEmitter extends DefaultTreeWalker
{
    public PythonDefaultEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        this.outputFileManager = outputFileManager;
        this.pythonParameters = pythonParameters;
        this.context = new TemplateDataContext(pythonParameters);
        this.generatorDescription = context.getGeneratorDescription().split("\\n");
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    protected boolean getWithPubsubCode()
    {
        return pythonParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return pythonParameters.getWithServiceCode();
    }

    protected boolean getWithSqlCode()
    {
        return pythonParameters.getWithSqlCode();
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioExtensionException
    {
        processSourceTemplate(templateName, templateData, zserioType.getPackage(), zserioType.getName());
    }

    protected void processSourceTemplate(String templateName, Object templateData,
            Package zserioPackage, String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioExtensionException
    {
        final File outDir = new File(pythonParameters.getOutputDir(), packageName.toFilesystemPath());
        final File outputFile = new File(outDir, getOutputFileName(outFileNameRoot));

        final boolean generate = !outputFileManager.checkTimestamps(outputFile) ||
                !checkGeneratorDescription(outputFile);
        if (generate)
        {
            FreeMarkerUtil.processTemplate(PYTHON_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                    false);
        }

        outputFileManager.registerOutputFile(outputFile, generate);
    }

    protected static List<String> readFreemarkerTemplate(String templateName) throws ZserioExtensionException
    {
        return FreeMarkerUtil.readFreemarkerTemplate(PYTHON_TEMPLATE_LOCATION + templateName);
    }

    static String getOutputFileName(String outFileNameRoot)
    {
        return outFileNameRoot + PYTHON_SOURCE_EXTENSION;
    }

    private boolean checkGeneratorDescription(File outputFile)
    {
        try (final Stream<String> lines = Files.lines(outputFile.toPath()))
        {
            final String[] generatorDescriptionCandidate =
                    lines.limit(generatorDescription.length).toArray(String[]::new);
            return Arrays.equals(generatorDescription, generatorDescriptionCandidate);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final OutputFileManager outputFileManager;
    private final PythonExtensionParameters pythonParameters;
    private final TemplateDataContext context;
    private final String[] generatorDescription;
}
