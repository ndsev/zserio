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

public class ByteArrayTest extends NumericArrayTestBase
{
    public ByteArrayTest()
    {
        super(new ByteArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here

    @Test
    public void constructorRead() throws IOException
    {
        final byte BYTE_VALUE = -10;
        final int NUM_BITS = 8;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeByte(BYTE_VALUE);
        os.close();
        baos.close();

        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());

        final ArrayWrapper array = factory.create(in, 1, NUM_BITS);

        assertEquals(1, array.length());
        assertEquals(BYTE_VALUE, array.elementAt(0));
    }

    @Test
    public void write() throws IOException
    {
        final ArrayWrapper array = createFilledArray();

        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        array.write(out, NUM_BITS);

        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < array.length(); i++)
        {
            assertEquals(i, in.readByte());
        }
    }

    @Test
    public void testHashCode()
    {
        final ArrayWrapper array = createFilledArray();

        int myHash = Util.HASH_SEED;
        for (long value : array)
        {
            myHash = myHash * Util.HASH_PRIME_NUMBER + (byte)value;
        }
        assertEquals(myHash, array.hashCode());

        assertEquals(factory.create(0).hashCode(), factory.create(0).hashCode());
        assertTrue(factory.create(0).equals(factory.create(0)));
    }

    private final static int NUM_BITS = 8;
}
