package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateImported.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiateImported readInstantiateImported = new InstantiateImported(reader);
        assertTrue(instantiateImported.equals(readInstantiateImported));
    }
}
