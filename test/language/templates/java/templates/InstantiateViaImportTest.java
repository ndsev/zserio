package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateViaImport.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiateViaImport readInstantiateViaImport = new InstantiateViaImport(reader);
        assertTrue(instantiateViaImport.equals(readInstantiateViaImport));
    }
}
