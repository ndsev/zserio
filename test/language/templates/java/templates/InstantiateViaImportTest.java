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

import templates.instantiate_via_import.InstantiateViaImport;
import templates.instantiate_via_import.pkg.U32;
import templates.instantiate_via_import.pkg.Test_string;

public class InstantiateViaImportTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateViaImport instantiateViaImport = new InstantiateViaImport();
        instantiateViaImport.setTest32(new U32(13));
        instantiateViaImport.setTestStr(new Test_string("test"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateViaImport.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateViaImport readInstantiateViaImport = new InstantiateViaImport(reader);
        reader.close();
        assertTrue(instantiateViaImport.equals(readInstantiateViaImport));
    }

    private static final File TEST_FILE = new File("test.bin");
}
