package zserio.extension.cpp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;
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
 * Base class for all C++ emitters.
 */
abstract class CppDefaultEmitter extends DefaultTreeWalker
{
    public CppDefaultEmitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters,
            PackedTypesCollector packedTypesCollector)
    {
        this.outputFileManager = outputFileManager;
        this.cppParameters = cppParameters;
        this.context = new TemplateDataContext(cppParameters, packedTypesCollector);
        this.generatorDescription = context.getGeneratorDescription().split("\\n");
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        packageSourceFileName = pkg.getLocation().getFileName();
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioType.getPackage().getPackageName(),
                zserioType.getName(), CPP_SOURCE_EXTENSION, true);
    }

    protected void processHeaderTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioExtensionException
    {
        processHeaderTemplate(
                templateName, templateData, zserioType.getPackage().getPackageName(), zserioType.getName());
    }

    protected void processHeaderTemplate(String templateName, Object templateData,
            PackageName zserioPackageName, String outFileName) throws ZserioExtensionException
    {
        processTemplate(
                templateName, templateData, zserioPackageName, outFileName, CPP_HEADER_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected boolean getWithPubsubCode()
    {
        return cppParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return cppParameters.getWithServiceCode();
    }

    protected boolean getWithSourcesAmalgamation()
    {
        return cppParameters.getWithSourcesAmalgamation();
    }

    protected boolean getWithSqlCode()
    {
        return cppParameters.getWithSqlCode();
    }

    private void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
            throws ZserioExtensionException
    {
        final File outDir = new File(cppParameters.getOutputDir(), packageName.toFilesystemPath());
        final boolean amalgamate = (getWithSourcesAmalgamation() && requestAmalgamate);
        final String outFileNameWithoutExtension = (amalgamate) ? getAmalgamFileNameRoot() : outFileNameRoot;
        final File outputFile = new File(outDir, outFileNameWithoutExtension + outputExtension);

        if (amalgamate)
        {
            final Boolean fileInfo = outputFileManager.getOutputFileInfo(outputFile);
            if (fileInfo != null)
            {
                if (fileInfo) // not skipped
                {
                    FreeMarkerUtil.processTemplate(CPP_TEMPLATE_LOCATION + templateName, templateData,
                            outputFile, CppDefaultEmitter.class, amalgamate);
                }
                return;
            }
            // else seen for the first time, normally generate
        }

        final boolean generate =
                !outputFileManager.checkTimestamps(outputFile) || !checkGeneratorDescription(outputFile);
        if (generate)
        {
            FreeMarkerUtil.processTemplate(CPP_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                    CppDefaultEmitter.class, amalgamate);
        }

        outputFileManager.registerOutputFile(outputFile, generate);
    }

    private boolean checkGeneratorDescription(File outputFile)
    {
        try (final Stream<String> lines = Files.lines(outputFile.toPath()))
        {
            final String[] generatorDescriptionCandidate =
                    lines.limit(generatorDescription.length).toArray(String[] ::new);
            return Arrays.equals(generatorDescription, generatorDescriptionCandidate);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private String getAmalgamFileNameRoot()
    {
        // strip possible directory
        final int lastSlashIndex = packageSourceFileName.lastIndexOf(File.separatorChar);
        final String sourceFileName = (lastSlashIndex == -1)
                ? packageSourceFileName
                : packageSourceFileName.substring(lastSlashIndex + 1);

        // strip extensions from source file name
        final int firstDotIndex = sourceFileName.indexOf('.');
        final String sourceFileNameRoot =
                (firstDotIndex == -1) ? sourceFileName : sourceFileName.substring(0, firstDotIndex);

        // convert main part of source file name to camel case
        final String[] words = sourceFileNameRoot.split("_");
        final StringBuilder builder = new StringBuilder();
        for (String word : words)
        {
            if (!word.isEmpty())
            {
                builder.append(
                        Character.toString(word.charAt(0)).toUpperCase(Locale.ENGLISH) + word.substring(1));
            }
        }

        final String amalgamFileNameRoot = builder.toString();

        return (amalgamFileNameRoot.isEmpty()) ? CPP_DEFAULT_AMALGAM_FILE_NAME_ROOT : amalgamFileNameRoot;
    }

    private static final String CPP_SOURCE_EXTENSION = ".cpp";
    private static final String CPP_HEADER_EXTENSION = ".h";
    private static final String CPP_TEMPLATE_LOCATION = "cpp/";

    private static final String CPP_DEFAULT_AMALGAM_FILE_NAME_ROOT = "Amalgamation";

    private final OutputFileManager outputFileManager;
    private final CppExtensionParameters cppParameters;
    private final TemplateDataContext context;
    private final String[] generatorDescription;

    private String packageSourceFileName = "";
}
