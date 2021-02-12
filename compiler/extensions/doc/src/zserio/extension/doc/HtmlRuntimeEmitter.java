package zserio.extension.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * HTML runtime emitter.
 *
 * It creates all external files stored in Jar file and used by generated HTML during runtime. For example,
 * bootstrap prerequisites stored in Jar file.
 */
class HtmlRuntimeEmitter
{
    static void emit(OutputFileManager outputFileManager,
            DocExtensionParameters docParameters) throws ZserioExtensionException
    {
        try
        {
            final String[] jarRuntimeSubdirs = { DocDirectories.CSS_DIRECTORY, DocDirectories.JS_DIRECTORY };
            for (String jarRuntimeSubdir : jarRuntimeSubdirs)
            {
                final List<String> jarResources = getJarRuntimeResources(jarRuntimeSubdir);
                for (String jarResource : jarResources)
                {
                    extractJarResource(
                            jarResource, outputFileManager, docParameters.getOutputDir(), jarRuntimeSubdir);
                }
            }
        }
        catch (IOException exception)
        {
            throw new ZserioExtensionException(exception.getMessage());
        }
        catch (URISyntaxException exception)
        {
            throw new ZserioExtensionException(exception.getMessage());
        }
    }

    private static List<String> getJarRuntimeResources(String jarRuntimeSubdir)
            throws IOException, URISyntaxException
    {
        final List<String> availableResources = new ArrayList<String>();
        final String resourceDir = JAR_RUNTIME_DIR + "/" + jarRuntimeSubdir;
        final URL jarUrl = HtmlRuntimeEmitter.class.getProtectionDomain().getCodeSource().getLocation();
        final JarFile jarFile = new JarFile(jarUrl.toURI().getPath());
        final Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements())
        {
            final JarEntry jarEntry = jarEntries.nextElement();

            if (!jarEntry.isDirectory())
            {
                final String jarEntryName = jarEntry.getName();
                if (jarEntryName.startsWith(resourceDir))
                    availableResources.add(jarEntryName);
            }
        }

        jarFile.close();

        return availableResources;
    }

    private static void extractJarResource(String jarResource, OutputFileManager outputFileManager,
            String outputDir, String outputSubDir) throws IOException, ZserioExtensionException
    {
        FileOutputStream writer = null;
        final InputStream reader = HtmlRuntimeEmitter.class.getResourceAsStream("/" + jarResource);
        try
        {
            if (reader != null)
            {
                final File outputFullDir = new File(outputDir, outputSubDir);
                if (!outputFullDir.exists() && !outputFullDir.mkdirs())
                    throw new IOException("Directory " + outputFullDir.toString() + " cannot be created!");

                final File outputFile = new File(outputFullDir, getResourceFileName(jarResource));
                writer = new FileOutputStream(outputFile);
                final byte[] buffer = new byte[16384];
                int bytesRead = 0;
                while ((bytesRead = reader.read(buffer)) != -1)
                    writer.write(buffer, 0, bytesRead);

                outputFileManager.registerOutputFile(outputFile);
            }
        }
        finally
        {
            try
            {
                if (writer != null)
                    writer.close();
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }
        }
    }

    private static String getResourceFileName(String jarResource)
    {
        final int lastSeparatorIndex = jarResource.lastIndexOf('/');

        return (lastSeparatorIndex == -1) ? jarResource : jarResource.substring(lastSeparatorIndex);
    }

    private static String JAR_RUNTIME_DIR = "zserio/extension/doc/runtime";
}
