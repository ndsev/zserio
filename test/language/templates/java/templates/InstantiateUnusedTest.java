package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_unused.U32;

public class InstantiateUnusedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final U32 u32 = new U32(13); // check that unused template is instantiated via the instantiate command

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        u32.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final U32 readU32 = new U32(reader);
        reader.close();
        assertTrue(u32.equals(readU32));
    }

    private static final File TEST_FILE = new File("test.bin");
}
