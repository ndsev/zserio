package zserio.extension.doc;

import java.io.File;
import java.io.IOException;

import zserio.extension.common.ZserioExtensionException;

/**
 * Converter for the generated dot files into svg format.
 *
 * This class runs external dot executable which converts dot files into svg format. It offers as well simple
 * test if the given dot executable works correctly.
 */
final class DotToSvgConverter
{
    public static boolean isDotExecAvailable(String dotExecutable)
    {
        final String commandWithArguments[] = {dotExecutable, "-V"};

        try
        {
            return runDotExecutable(commandWithArguments);
        }
        catch (ZserioExtensionException exception)
        {
            return false;
        }
    }

    public static void convert(String dotExecutable, File inputDotFile, File outputSvgFile)
            throws ZserioExtensionException
    {
        final String commandWithArguments[] = {
                dotExecutable, inputDotFile.toString(), "-T", "svg", "-o", outputSvgFile.toString()};
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
