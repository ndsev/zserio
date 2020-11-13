package zserio.extension.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * File utilities for Zserio extensions.
 */
public class FileUtil
{
    /**
     * Creates output directory using given file.
     *
     * @param outputFile Output file for which to create output directory.
     *
     * @throws ZserioExtensionException Throws if output directory cannot be created.
     */
    public static void createOutputDirectory(File outputFile) throws ZserioExtensionException
    {
        final File parentDir = outputFile.getParentFile();
        if (parentDir.exists())
        {
            if (!parentDir.isDirectory())
                throw new ZserioExtensionException("Can't create ouput directory because file with the same " +
                        "name already exists: " + parentDir.toString());
        }
        else
        {
            if (!parentDir.mkdirs())
                throw new ZserioExtensionException("Can't create output directory: " + parentDir.toString());
        }
    }

    /**
     * Creates writer for given file.
     *
     * @param outputFile Output file for which to create writer.
     *
     * @return Created writer.
     *
     * @throws ZserioExtensionException Throws if writer cannot be created.
     */
    public static PrintWriter createWriter(File outputFile) throws ZserioExtensionException
    {
        try
        {
            return new PrintWriter(outputFile, "UTF-8");
        }
        catch (IOException exception)
        {
            throw new ZserioExtensionException(exception.getMessage());
        }
    }
}
