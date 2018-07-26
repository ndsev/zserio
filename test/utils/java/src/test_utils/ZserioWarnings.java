package test_utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The class handles Zserio warnings stored in the text file during compilation.
 */
public class ZserioWarnings
{
    /**
     * Constructor.
     *
     * @throws IOException Throws in case of any file handling error.
     */
    public ZserioWarnings() throws IOException
    {
        zserioWarnings = FileUtil.readLines(new File(ZSERIO_WARNINGS_FILE_NAME));
    }

    /**
     * Checks if given Zserio warning is present.
     *
     * @param warning Warning to check.
     *
     * @return true if given Zserio warning is present, otherwise false.
     */
    public boolean isPresent(String warning)
    {
        for (String zserioWarning : zserioWarnings)
            if (zserioWarning.contains(warning))
                return true;

        return false;
    }

    /**
     * Checks if there is no Zserio warning.
     *
     * @return true if there is no Zserio warning, otherwise false.
     */
    public boolean isEmpty()
    {
        return zserioWarnings.isEmpty();
    }

    /**
     * Returns the number of Zserio warnings.
     *
     * @return Number of Zserio warnings.
     */
    public int getCount()
    {
        return zserioWarnings.size();
    }

    private static String ZSERIO_WARNINGS_FILE_NAME = "zserio_log.txt";

    private List<String> zserioWarnings;
}
