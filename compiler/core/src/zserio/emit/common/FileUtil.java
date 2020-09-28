package zserio.emit.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * File utilities for Zserio emitters.
 */
public class FileUtil
{
    /**
     * Creates output directory using given file.
     *
     * @param outputFile Output file for which to create output directory.
     *
     * @throws ZserioEmitException Throws if output directory cannot be created.
     */
    public static void createOutputDirectory(File outputFile) throws ZserioEmitException
    {
        final File parentDir = outputFile.getParentFile();
        if (parentDir.exists())
        {
            if (!parentDir.isDirectory())
                throw new ZserioEmitException("Can't create ouput directory because file with the same " +
                        "name already exists: " + parentDir.toString());
        }
        else
        {
            if (!parentDir.mkdirs())
                throw new ZserioEmitException("Can't create output directory: " + parentDir.toString());
        }
    }

    /**
     * Creates writer for given file.
     *
     * @param outputFile Output file for which to create writer.
     *
     * @return Created writer.
     *
     * @throws ZserioEmitException Throws if writer cannot be created.
     */
    public static PrintWriter createWriter(File outputFile) throws ZserioEmitException
    {
        try
        {
            return new PrintWriter(outputFile, "UTF-8");
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
    }
}
