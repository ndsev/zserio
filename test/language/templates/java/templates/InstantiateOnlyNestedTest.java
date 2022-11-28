package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_only_nested.InstantiateOnlyNested;
import templates.instantiate_only_nested.N32;
import templates.instantiate_only_nested.pkg.Test_uint32;

public class InstantiateOnlyNestedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateOnlyNested instantiateOnlyNested = new InstantiateOnlyNested();
        instantiateOnlyNested.setTest32(new Test_uint32(new N32(13)));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateOnlyNested.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final InstantiateOnlyNested readInstantiateOnlyNested = new InstantiateOnlyNested(reader);
        assertTrue(instantiateOnlyNested.equals(readInstantiateOnlyNested));
    }

}
