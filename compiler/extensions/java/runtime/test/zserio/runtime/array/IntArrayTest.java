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

public class IntArrayTest extends NumericArrayTestBase
{
    public IntArrayTest()
    {
        super(new IntArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here

    @Test
    public void constructorRead() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeInt(18);
        os.close();
        baos.close();

        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());

        final ArrayWrapper array = factory.create(in, 1, NUM_BITS);

        assertEquals(1, array.length());
        assertEquals(18, array.elementAt(0));
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
            assertEquals(i, in.readInt());
        }
    }

    @Test
    public void testHashCode()
    {
        final ArrayWrapper array = createFilledArray();

        int myHash = Util.HASH_SEED;
        for (long value : array)
        {
            myHash = myHash * Util.HASH_PRIME_NUMBER + (int)value;
        }
        assertEquals(myHash, array.hashCode());

        assertEquals(factory.create(0).hashCode(), factory.create(0).hashCode());
        assertTrue(factory.create(0).equals(factory.create(0)));
    }

    private final static int NUM_BITS = 32;
}
