package zserio.emit.doc;

import java.io.File;
import java.io.IOException;

/**
 * The class converts the generated dot files into svg format.
 */
class DotFileConvertor
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

        return runDotExecutable(commandWithArguments);
    }

    /**
     * Converts the given dot file into svg file.
     *
     * @param dotExecutable Dot executable to use.
     * @param inputDotFile Name of the dot file to convert.
     * @param outputSvgFile Name of the svg file to generate.
     *
     * @return True in case of success, otherwise false.
     */
    public static boolean convertToSvg(String dotExecutable, File inputDotFile, File outputSvgFile)
    {
        final String commandWithArguments[] = { dotExecutable, inputDotFile.toString(), "-T", "svg", "-o",
                                                outputSvgFile.toString() };

        return runDotExecutable(commandWithArguments);
    }

    private static boolean runDotExecutable(String[] commandWithArguments)
    {
        boolean success = false;
        try
        {
            final Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(commandWithArguments);
            if (process.waitFor() == 0)
                success = true;
        }
        catch (IOException exception)
        {
        }
        catch (InterruptedException exception)
        {
        }

        return success;
    }
}
