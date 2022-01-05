package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileBitStreamReaderTest
{
    @Test
    public void readNonexistentFile() throws IOException
    {
        final File tempFile = File.createTempFile(TempFileNamePrefix, null);
        if (tempFile.exists())
        {
            if (!tempFile.delete())
                throw new RuntimeException("can't delete temporary file " + tempFile);
        }

        // this is of course prone to race conditions but we don't have to be paranoid here
        if (tempFile.exists())
            throw new IllegalArgumentException("a file that must not exist exists");

        assertThrows(IOException.class, () -> new FileBitStreamReader(tempFile),
                "opening a non-existent file succeeded");
    }

    @Test
    public void read() throws IOException
    {
        final byte data[] = { 0x11, 0x22 };

        final File tempFile = File.createTempFile(TempFileNamePrefix, null);

        final DataOutputStream writer = new DataOutputStream(new FileOutputStream(tempFile));
        try
        {
            for (byte value : data)
            {
                writer.writeByte(value);
            }
        }
        finally
        {
            writer.close();
        }

        final FileBitStreamReader reader = new FileBitStreamReader(tempFile);
        try
        {
            for (byte value : data)
            {
                assertEquals(value, reader.readByte());
            }
        }
        finally
        {
            reader.close();
        }

        if (!tempFile.delete())
            throw new RuntimeException("can't delete temporary file " + tempFile);
    }

    private static final String TempFileNamePrefix = "FileBitStreamReaderTest";
}
