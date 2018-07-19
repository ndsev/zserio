package zserio.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import java.util.HashSet;
import java.util.Set;

/**
 * The manager which holds all Zserio input file names.
 */
public class InputFileManager
{
    /**
     * Empty constructor.
     */
    public InputFileManager()
    {
        inputFiles = new HashSet<String>();
    }

    /**
     * Registers Zserio input file name.
     *
     * @param fileName Input file name to register.
     */
    public void registerFile(String fileName)
    {
        inputFiles.add(fileName);
    }

    /**
     * Returns true if Zserio input file name has been already registered.
     *
     * @param fileName Zserio input file name to check.
     */
    public boolean isFileRegistered(String fileName)
    {
        return inputFiles.contains(fileName);
    }

    /**
     * Checks the content of Zserio input files.
     *
     * It fires warning if Zserio input file contains
     * - non UTF-8 encoding characters
     * - TAB characters
     *
     * @throws FileNotFoundException Throws if input file was not found.
     * @throws IOException Throws in case of input file read error.
     */
    public void checkFiles() throws FileNotFoundException, IOException
    {
        for (String fileName : inputFiles)
        {
            final File file = new File(fileName);
            final FileInputStream inputStream = new FileInputStream(file);
            final byte fileContent[] = new byte[(int) file.length()];
            try
            {
                if (inputStream.read(fileContent) == -1)
                    throw new IOException("Error during reading of file " + fileName + "!");
            }
            finally
            {
                inputStream.close();
            }

            checkUtf8Encoding(fileName, fileContent);
            checkNonPrintableCharacters(fileName, fileContent);
        }
    }

    private void checkUtf8Encoding(String fileName, byte[] fileContent)
    {
        try
        {
            final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.decode(ByteBuffer.wrap(fileContent));
        }
        catch (CharacterCodingException exception)
        {
            ZserioToolPrinter.printWarning(fileName, "Found non-UTF8 encoded characters.");
        }
    }

    private void checkNonPrintableCharacters(String fileName, byte[] fileContent)
    {
        final String content = new String(fileContent, Charset.forName("UTF-8"));

        if (content.indexOf('\t') >= 0)
            ZserioToolPrinter.printWarning(fileName, "Found tab characters.");

        for (int i = 0; i < content.length(); ++i)
        {
            final char character = content.charAt(i);
            if (character < '\u0020' && character != '\r' && character != '\n' && character != '\t')
            {
                ZserioToolPrinter.printWarning(fileName, "Found non-printable ASCII characters.");
                break;
            }
        }
    }

    private final Set<String> inputFiles;
}
