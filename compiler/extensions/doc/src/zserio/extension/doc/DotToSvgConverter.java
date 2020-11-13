package zserio.extension.doc;

import java.io.File;
import java.io.IOException;

import zserio.extension.common.ZserioExtensionException;

/**
 * The class converts the generated dot files into svg format.
 */
class DotToSvgConverter
{
    /**
     * Checks if the dot executable is available in the system.
     *
     * @param dotExecutable Dot executable to use.
     *
     * @return True if dot executable is available, otherwise false.
     */
    public static boolean isDotExecAvailable(String dotExecutable)
    {
        final String commandWithArguments[] = { dotExecutable, "-V" };

        try
        {
            return runDotExecutable(commandWithArguments);
        }
        catch (ZserioExtensionException exception)
        {
            return false;
        }
    }

    /**
     * Converts the given dot file into svg file.
     *
     * @param dotExecutable Dot executable to use.
     * @param inputDotFile Name of the dot file to convert.
     * @param outputSvgFile Name of the svg file to generate.
     *
     * @throw ZserioExtensionException In case of fatal error.
     */
    public static void convert(String dotExecutable, File inputDotFile, File outputSvgFile)
            throws ZserioExtensionException
    {
        final String commandWithArguments[] = { dotExecutable, inputDotFile.toString(), "-T", "svg", "-o",
                                                outputSvgFile.toString() };
        if (!runDotExecutable(commandWithArguments))
            throw new ZserioExtensionException("Failure to convert '" + inputDotFile + "' to SVG format!");
    }

    private static boolean runDotExecutable(String[] commandWithArguments) throws ZserioExtensionException
    {
        try
        {
            final Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(commandWithArguments);

            // wait for the result code
            return (process.waitFor() == 0);
        }
        catch (IOException exception)
        {
            throw new ZserioExtensionException("Dot executable: " + exception.getMessage());
        }
        catch (InterruptedException exception)
        {
            throw new ZserioExtensionException("Dot executable: " + exception.getMessage());
        }
    }
}
