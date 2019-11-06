package subtypes;

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

import subtypes.subtype_imported.SubtypeImported;

public class SubtypeImportedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final SubtypeImported subtypeImported = new SubtypeImported(new subtypes.subtype_imported.Test(42));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        subtypeImported.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        SubtypeImported readSubtypeImported = new SubtypeImported(reader);
        reader.close();
        assertTrue(subtypeImported.equals(readSubtypeImported));
    }

    private static final File TEST_FILE = new File("test.bin");
}
