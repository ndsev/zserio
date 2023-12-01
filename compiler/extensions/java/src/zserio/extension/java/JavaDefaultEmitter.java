package zserio.extension.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base class for all Java emitters.
 */
abstract class JavaDefaultEmitter extends DefaultTreeWalker
{
    public JavaDefaultEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
            PackedTypesCollector packedTypesCollector)
    {
        this.outputFileManager = outputFileManager;
        this.javaParameters = javaParameters;
        this.packedTypesCollector = packedTypesCollector;
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        super.beginPackage(pkg);

        if (context == null)
        {
            context = new TemplateDataContext(javaParameters, pkg.getPackageName(), packedTypesCollector);
        }
    }

    protected boolean getWithPubsubCode()
    {
        return javaParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return javaParameters.getWithServiceCode();
    }

    protected boolean getWithSqlCode()
    {
        return javaParameters.getWithSqlCode();
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioType, zserioType.getName());
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioType.getPackage(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, Package zserioPackage,
            String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    private void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioExtensionException
    {
        final File outDir = new File(javaParameters.getOutputDir(), packageName.toFilesystemPath());
        final File outputFile = new File(outDir, outFileNameRoot + JAVA_SOURCE_EXTENSION);

        final boolean generate = !outputFileManager.checkTimestamps(outputFile) ||
                !checkGeneratorDescription(outputFile);
        if (generate)
        {
            FreeMarkerUtil.processTemplate(JAVA_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                    false);
        }

        outputFileManager.registerOutputFile(outputFile, generate);
    }

    private boolean checkGeneratorDescription(File outputFile)
    {
        try (final Stream<String> lines = Files.lines(outputFile.toPath()))
        {
            final String[] generatorDescription = context.getGeneratorDescription().split("\\n");
            final String[] generatorDescriptionCandidate =
                    lines.limit(generatorDescription.length).toArray(String[]::new);
            return Arrays.equals(generatorDescription, generatorDescriptionCandidate);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";

    private final OutputFileManager outputFileManager;
    private final JavaExtensionParameters javaParameters;
    private final PackedTypesCollector packedTypesCollector;

    private TemplateDataContext context = null;
}
