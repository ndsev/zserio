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

import templates.instantiate_via_single_import.InstantiateViaSingleImport;
import templates.instantiate_via_single_import.pkg.U32;
import templates.instantiate_via_single_import.pkg.Test_string;

public class InstantiateViaSingleImportTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateViaSingleImport instantiateViaSingleImport = new InstantiateViaSingleImport();
        instantiateViaSingleImport.setTest32(new U32(13));
        instantiateViaSingleImport.setTestStr(new Test_string("test"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateViaSingleImport.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateViaSingleImport readInstantiateViaSingleImport = new InstantiateViaSingleImport(reader);
        reader.close();
        assertTrue(instantiateViaSingleImport.equals(readInstantiateViaSingleImport));
    }

    private static final File TEST_FILE = new File("test.bin");
}
