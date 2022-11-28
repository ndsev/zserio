package zserio.runtime.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A bit stream reader using file.
 *
 * @deprecated Please use {@link SerializeUtil#deserializeFromFile} instead.
 */
@Deprecated
public class FileBitStreamReader extends ByteArrayBitStreamReader
{
    /**
     * Creates a new file bit stream reader with the specified file name.
     *
     * @param filename File name to create bit stream reader from.
     *
     * @throws IOException If file manipulation error occurred.
     */
    public FileBitStreamReader(final String filename) throws IOException
    {
        this(new File(filename));
    }

    /**
     * Creates a new file bit stream reader with the specified file.
     *
     * @param file File to create bit stream reader from.
     *
     * @throws IOException If file manipulation error occurred.
     */
    public FileBitStreamReader(final File file) throws IOException
    {
        super(initBuffer(file));
    }

    private static byte[] initBuffer(final File file) throws IOException
    {
        try (final InputStream is = new FileInputStream(file))
        {
            final long length = file.length();
            if (length > Integer.MAX_VALUE)
                throw new UnsupportedOperationException("FileBitStreamReader: File with length " + length +
                        " is too large.");

            // Create the byte array to hold the data
            final byte[] buffer = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length && (numRead = is.read(buffer, offset, buffer.length - offset)) >= 0)
            {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < buffer.length)
                throw new IOException("FileBitStreamReader: Could not completely read file " + file.toString() +
                        ".");

            return buffer;
        }
    }
}
