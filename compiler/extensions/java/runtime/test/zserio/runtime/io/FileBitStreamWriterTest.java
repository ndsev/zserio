package zserio.runtime.io;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

public class FileBitStreamWriterTest
{
    @Test
    public void write() throws IOException
    {
        final byte data[] = { 0x11, 0x22 };

        final File tempFile = File.createTempFile(TempFileNamePrefix, null);

        final FileBitStreamWriter writer = new FileBitStreamWriter(tempFile);
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

        final DataInputStream reader = new DataInputStream(new FileInputStream(tempFile));
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

    private static final String TempFileNamePrefix = "FileBitStreamWriterTest";
}
