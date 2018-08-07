package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zserio.runtime.Util;

public class UnsignedLongArrayTest extends UnsignedFixedIntegerArrayTestBase
{
    public UnsignedLongArrayTest()
    {
        super(new UnsignedLongArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here
    @Test
    public void testHashCode()
    {
        final ArrayWrapper array = createFilledArray();

        int myHash = Util.HASH_SEED;
        for (long value : array)
        {
            myHash = myHash * Util.HASH_PRIME_NUMBER + (int)(value ^ (value >>> 32));
        }
        assertEquals(myHash, array.hashCode());

        assertEquals(factory.create(0).hashCode(), factory.create(0).hashCode());
        assertTrue(factory.create(0).equals(factory.create(0)));
    }

    @Override
    protected int getReadWriteTestNumBits()
    {
        return Long.SIZE - 1; // use the maximal number of bits the array allows
    }

    @Override
    protected long[] getReadWriteTestData()
    {
        final long[] data =
        {
            0x7111111111111111L,
            0x7222222222222222L,
            0x7333333333333333L,
            0x7444444444444444L,
            0x7555555555555555L,

            0x71111111111111L,
            0x72222222222222L,
            0x73333333333333L,
            0x74444444444444L,
            0x75555555555555L
        };

        return data;
    }
}
