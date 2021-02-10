package zserio.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The manager which allows to get info from resources on class path.
 */
class ResourceManager
{
    /**
     * Constructor.
     */
    public ResourceManager()
    {
        lastModifiedTime = getLastModifiedResourceTime();
    }

    /**
     * Gets last modified timestamp of last modified Zserio resource .
     *
     * @return Last modified timestamp (in milliseconds since epoch).
     */
    public long getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    private long getLastModifiedResourceTime()
    {
        final String classPath = System.getProperty("java.class.path");

        if (classPath == null || classPath.isEmpty())
        {
            ZserioToolPrinter.printWarning("ResourceManager: Cannot get class path!");
            return 0L;
        }

        final String[] classPathElements = classPath.split(File.pathSeparator);

        long lastModifiedResourceTime = 0L;

        for (String element : classPathElements)
        {
            final long lastModifiedElementTime = getLastModifiedResourceTime(element);
            if (lastModifiedElementTime > lastModifiedResourceTime)
                lastModifiedResourceTime = lastModifiedElementTime;
            else if (lastModifiedElementTime == 0L)
                return 0L; // warning already reported
        }

        return lastModifiedResourceTime;
    }

    private long getLastModifiedResourceTime(String resourcePath)
    {
        // works for both directories and files as an argument
        // (note that for jar file it returns its last modified time)
        try (Stream<Path> walk = Files.walk(Paths.get(resourcePath)))
        {
            long lastModifiedResourceTime = 0L;

            for (Iterator<Path> it = walk.iterator(); it.hasNext();)
            {
                final File entry = it.next().toFile();
                final long lastModifiedEntryTime = entry.lastModified();
                if (lastModifiedEntryTime > lastModifiedResourceTime)
                {
                    lastModifiedResourceTime = lastModifiedEntryTime;
                }
                else if (lastModifiedEntryTime == 0L)
                {
                    ZserioToolPrinter.printWarning(
                            "ResourceManager: Failed to get timestamp of resource: '" + entry + "'!");
                    return 0L;
                }
            }

            return lastModifiedResourceTime;
        }
        catch (IOException e)
        {
            ZserioToolPrinter.printWarning(
                    "ResourceManager: Failed to get timestamp of resource: '" + resourcePath + "'!");
            return 0L;
        }
    }

    private final long lastModifiedTime;
}
