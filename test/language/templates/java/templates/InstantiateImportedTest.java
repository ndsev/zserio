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

import templates.instantiate_imported.InstantiateImported;
import templates.instantiate_imported.pkg.U32;
import templates.instantiate_imported.Test_string;

public class InstantiateImportedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateImported instantiateImported = new InstantiateImported();
        instantiateImported.setTest32(new U32(13));
        instantiateImported.setTestStr(new Test_string("test"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateImported.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateImported readInstantiateImported = new InstantiateImported(reader);
        reader.close();
        assertTrue(instantiateImported.equals(readInstantiateImported));
    }

    private static final File TEST_FILE = new File("test.bin");
}
