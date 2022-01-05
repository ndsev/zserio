package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_not_imported.InstantiateNotImported;
import templates.instantiate_not_imported.pkg.Test_uint32;
import templates.instantiate_not_imported.pkg.Test_string;

public class InstantiateNotImportedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateNotImported instantiateNotImported = new InstantiateNotImported();
        instantiateNotImported.setTest32(new Test_uint32(13));
        instantiateNotImported.setTestStr(new Test_string("test"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateNotImported.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateNotImported readInstantiateNotImported = new InstantiateNotImported(reader);
        reader.close();
        assertTrue(instantiateNotImported.equals(readInstantiateNotImported));
    }

    private static final File TEST_FILE = new File("test.bin");
}
