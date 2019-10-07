package test_utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The class handles Zserio errors stored in the text file during compilation.
 */
public class ZserioErrors
{
    /**
     * Constructor.
     *
     * @throws IOException Throws in case of any file handling error.
     */
    public ZserioErrors() throws IOException
    {
        zserioErrors = FileUtil.readLines(new File(ZSERIO_ERRORS_FILE_NAME));
    }

    /**
     * Checks if given Zserio error is present.
     *
     * @param error Error to check.
     *
     * @return true if given Zserio error is present, false otherwise.
     */
    public boolean isPresent(String error)
    {
        for (String zserioError : zserioErrors)
            if (zserioError.contains(error))
                return true;

        return false;
    }

    /**
     * Checks if given errors are present in the given order.
     *
     * @param errors List of errors defining the requested order.
     *
     * @return true if given errors are present in right order, false otherwise.
     */
    public boolean isPresent(String[] errors)
    {
        for (int i = 0, j = 0; i < zserioErrors.size(); ++i)
        {
            final String error = errors[j];
            if (zserioErrors.get(i).contains(error) && ++j == errors.length)
                return true;
        }

        return false;
    }

    private static String ZSERIO_ERRORS_FILE_NAME = "zserio_log.txt";

    private List<String> zserioErrors;
}
