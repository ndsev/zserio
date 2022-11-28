package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_simple.InstantiateSimple;
import templates.instantiate_simple.U32;

public class InstantiateSimpleTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateSimple instantiateSimple = new InstantiateSimple();
        instantiateSimple.setTest(new U32(13));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateSimple.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiateSimple readInstantiateSimple = new InstantiateSimple(reader);
        assertTrue(instantiateSimple.equals(readInstantiateSimple));
    }
}
