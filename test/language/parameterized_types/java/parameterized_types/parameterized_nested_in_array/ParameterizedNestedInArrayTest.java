package parameterized_types.parameterized_nested_in_array;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class ParameterizedNestedInArrayTest
{
    @Test
    public void writeRead()
    {
        final Holder holder = new Holder(new Element[] {new Element(new Parameterized((short)5, (long)6))},
                new Element[] {new Element(new Parameterized((short)5, (long)6))});

        final BitBuffer bitBuffer = SerializeUtil.serialize(holder);
        final Holder readHolder = SerializeUtil.deserialize(Holder.class, bitBuffer);
        assertEquals(holder, readHolder);
    }

    @Test
    public void parameterCheckExceptionInArray() throws IOException
    {
        final Holder holder =
                new Holder(new Element[] {new Element(new Parameterized((short)6, (long)7))}, new Element[0]);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        assertThrows(ZserioError.class, () -> holder.write(writer));
    }

    @Test
    public void parameterCheckExceptionInPackedArray() throws IOException
    {
        final Holder holder =
                new Holder(new Element[0], new Element[] {new Element(new Parameterized((short)6, (long)7))});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        assertThrows(ZserioError.class, () -> holder.write(writer));
    }
}
