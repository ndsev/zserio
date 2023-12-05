package test_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The class provides help methods for manipulation with files.
 */
public final class FileUtil
{
    /**
     * Reads all lines from the given text file.
     *
     * @param file File to read.
     *
     * @return List of read lines.
     *
     * @throws IOException Throws in case of any file handling error.
     */
    public static List<String> readLines(File file) throws IOException
    {
        final InputStreamReader inputStreamReader =
                new InputStreamReader(new FileInputStream(file), "UTF-8");
        final BufferedReader bufferReader = new BufferedReader(inputStreamReader);
        final List<String> lines = new ArrayList<String>();
        try
        {
            while (true)
            {
                final String line = bufferReader.readLine();
                if (line == null)
                    break;
                lines.add(line);
            }
        }
        finally
        {
            bufferReader.close();
        }

        return lines;
    }

    /**
     * Deletes file if exists.
     *
     * @param file File to delete.
     *
     * @return List of read lines.
     */
    public static void deleteFileIfExists(File file)
    {
        if (file.exists())
        {
            if (!file.delete())
                throw new RuntimeException("Can't delete file " + file);
        }
    }
}
