package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_via_single_import.InstantiateViaSingleImport;
import templates.instantiate_via_single_import.pkg.Test_string;
import templates.instantiate_via_single_import.pkg.U32;

public class InstantiateViaSingleImportTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateViaSingleImport instantiateViaSingleImport = new InstantiateViaSingleImport();
        instantiateViaSingleImport.setTest32(new U32(13));
        instantiateViaSingleImport.setTestStr(new Test_string("test"));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateViaSingleImport.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiateViaSingleImport readInstantiateViaSingleImport =
                new InstantiateViaSingleImport(reader);
        assertTrue(instantiateViaSingleImport.equals(readInstantiateViaSingleImport));
    }
}
