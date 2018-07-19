package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.junit.Test;

import zserio.runtime.Util;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UnsignedByteArrayTest extends UnsignedFixedIntegerArrayTestBase
{
    public UnsignedByteArrayTest()
    {
        super(new UnsignedByteArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here
    @Test
    public void constructorRead() throws IOException
    {
        final int HIGH_NIBBLE = 1;
        final int LOW_NIBBLE = 2;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeByte((HIGH_NIBBLE << 4) | LOW_NIBBLE);
        os.close();
        baos.close();

        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());

        final ArrayWrapper array = factory.create(in, 2, 4);

        assertEquals(2, array.length());
        assertEquals(HIGH_NIBBLE, array.elementAt(0));
        assertEquals(LOW_NIBBLE, array.elementAt(1));
    }

    @Test
    public void write() throws IOException
    {
        // UnsignedByteArray stores at most 8 bits of data
        final int COUNT = 16; // all 4 bits set + 1 (upper bound)
        final UnsignedByteArray array = new UnsignedByteArray(COUNT);

        for (int i = 0; i < COUNT; i++)
        {
            array.setElementAt((short)i, i);
        }

        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        array.write(out, 4);

        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < array.length(); i += 2)
        {
            final int read = in.readUnsignedByte();
            final int expected = (i << 4) | (i + 1);
            assertEquals(expected, read);
        }
    }

    @Test
    public void testHashCode()
    {
        final ArrayWrapper array = createFilledArray();

        int myHash = Util.HASH_SEED;
        for (long value : array)
        {
            myHash = myHash * Util.HASH_PRIME_NUMBER + (short)value;
        }
        assertEquals(myHash, array.hashCode());

        assertEquals(factory.create(0).hashCode(), factory.create(0).hashCode());
        assertTrue(factory.create(0).equals(factory.create(0)));
    }

    @Override
    protected int getReadWriteTestNumBits()
    {
        return Byte.SIZE; // use the maximal number of bits the array allows
    }

    @Override
    protected long[] getReadWriteTestData()
    {
        final long[] data =
        {
            0xf1,
            0xf2,
            0xf3,
            0xf4,
            0xf5,
            0xff
        };

        return data;
    }
}
