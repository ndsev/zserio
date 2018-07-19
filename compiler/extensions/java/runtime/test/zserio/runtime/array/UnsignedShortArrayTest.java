package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zserio.runtime.Util;

public class UnsignedShortArrayTest extends UnsignedFixedIntegerArrayTestBase
{
    public UnsignedShortArrayTest()
    {
        super(new UnsignedShortArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here
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

    @Override
    protected int getReadWriteTestNumBits()
    {
        return Short.SIZE; // use the maximal number of bits the array allows
    }

    @Override
    protected long[] getReadWriteTestData()
    {
        final long[] data =
        {
            0xf111,
            0xf222,
            0xf333,
            0xf444,
            0xf555,
            0xffff
        };

        return data;
    }
}
