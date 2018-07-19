package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zserio.runtime.Util;

public class UnsignedIntArrayTest extends UnsignedFixedIntegerArrayTestBase
{
    public UnsignedIntArrayTest()
    {
        super(new UnsignedIntArrayFactory());
    }

    // common tests are inherited while type-specific tests are defined here
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
        return Integer.SIZE;
    }

    @Override
    protected long[] getReadWriteTestData()
    {
        final long[] data =
        {
            0xf1111111L,
            0xf2222222L,
            0xf3333333L,
            0xf4444444L,
            0xf5555555L,
            0xffffffffL
        };

        return data;
    }
}
