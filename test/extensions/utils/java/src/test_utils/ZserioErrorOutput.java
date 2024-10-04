package test_utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The class handles Zserio error messages stored in the text file during compilation.
 */
public final class ZserioErrorOutput
{
    /**
     * Constructor.
     *
     * @throws IOException Throws in case of any file handling error.
     */
    public ZserioErrorOutput() throws IOException
    {
        zserioErrorOutput = FileUtil.readLines(new File(ZSERIO_ERROR_OUTPUT_FILE_NAME));
    }

    /**
     * Checks if given Zserio message is present.
     *
     * @param message Message to check.
     *
     * @return true if given Zserio message is present, otherwise false.
     */
    public boolean isPresent(String message)
    {
        for (String zserioMessage : zserioErrorOutput)
            if (zserioMessage.contains(message))
                return true;

        return false;
    }

    /**
     * Checks if given messages are present in the given order.
     *
     * @param messages List of messages defining the requested order.
     *
     * @return true if given messages are present in right order, false otherwise.
     */
    public boolean isPresent(String[] messages)
    {
        for (int i = 0, j = 0; i < zserioErrorOutput.size(); ++i)
        {
            final String message = messages[j];
            if (zserioErrorOutput.get(i).contains(message) && ++j == messages.length)
                return true;
        }

        return false;
    }

    private static String ZSERIO_ERROR_OUTPUT_FILE_NAME = "zserio_log.txt";

    private List<String> zserioErrorOutput;
}
