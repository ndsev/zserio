package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateNotImported.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiateNotImported readInstantiateNotImported = new InstantiateNotImported(reader);
        assertTrue(instantiateNotImported.equals(readInstantiateNotImported));
    }
}
