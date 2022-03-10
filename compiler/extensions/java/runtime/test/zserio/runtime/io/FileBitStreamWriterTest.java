package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileBitStreamWriterTest
{
    @Test
    public void write() throws IOException
    {
        final byte data[] = { 0x11, 0x22 };

        final File tempFile = File.createTempFile(TempFileNamePrefix, null);

        try (final FileBitStreamWriter writer = new FileBitStreamWriter(tempFile))
        {
            for (byte value : data)
            {
                writer.writeByte(value);
            }
        }

        try (final DataInputStream reader = new DataInputStream(new FileInputStream(tempFile)))
        {
            for (byte value : data)
            {
                assertEquals(value, reader.readByte());
            }
        }

        if (!tempFile.delete())
            throw new RuntimeException("can't delete temporary file " + tempFile);
    }

    private static final String TempFileNamePrefix = "FileBitStreamWriterTest";
}
