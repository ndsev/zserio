package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_unused.U32;

public class InstantiateUnusedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final U32 u32 = new U32(13); // check that unused template is instantiated via the instantiate command

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        u32.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final U32 readU32 = new U32(reader);
        assertTrue(u32.equals(readU32));
    }
}
