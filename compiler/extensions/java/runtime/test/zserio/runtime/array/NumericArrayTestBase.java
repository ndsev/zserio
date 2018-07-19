package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

public abstract class NumericArrayTestBase
{
    public NumericArrayTestBase(ArrayFactory factory)
    {
        this.factory = factory;
    }

    /**
     * Test arrays with different lengths.
     */
    @Test
    public void length()
    {
        ArrayWrapper intArray = factory.create(LENGTH);
        assertEquals(LENGTH, intArray.length());

        intArray = factory.create(0);
        assertEquals(0, intArray.length());

        intArray = factory.create(LENGTH - 1);
        assertEquals(LENGTH - 1, intArray.length());
    }

    @Test
    public void arrayFromArray()
    {
        final ArrayWrapper intArray = factory.create(TEST_DATA, 0, TEST_DATA.length);

        assertEquals(TEST_DATA.length, intArray.length());
        for (int i = 0; i < TEST_DATA.length; i++)
        {
            assertEquals(TEST_DATA[i], intArray.elementAt(i));
        }
    }

    @Test
    public void arrayFromArrayWithBits()
    {
        final ArrayWrapper intArray = factory.create(TEST_DATA, 0, TEST_DATA.length);

        assertEquals(TEST_DATA.length, intArray.length());
        for (int i = 0; i < TEST_DATA.length; i++)
        {
            assertEquals(TEST_DATA[i], intArray.elementAt(i));
        }
    }

    @Test
    public void elementAt()
    {
        final ArrayWrapper array = createFilledArray();

        for (int i = 0; i < array.length(); i++)
        {
            assertEquals(i, array.elementAt(i));
        }

        for (int i = 0; i < array.length(); i++)
        {
            array.setElementAt(1, i);
        }
        for (int i = 0; i < array.length(); i++)
        {
            assertEquals(1, array.elementAt(i));
        }
    }

    @Test
    public void sum() throws Exception
    {
        final ArrayWrapper array = createFilledArray();

        final int expectedSum = (array.length() - 1)*array.length() / 2;

        assertEquals(expectedSum, array.sum());
    }

    @Test
    public void subRange()
    {
        final ArrayWrapper array = factory.create(LENGTH);

        for (int i = 0; i < array.length(); i++)
        {
            array.setElementAt(i, i);
        }

        final int offset = LENGTH / 2;
        final int length = array.length() - offset;
        final ArrayWrapper subarray = array.subRange(offset, length);

        assertEquals(length, subarray.length());

        for (int i = 0; i < length; i++)
        {
            assertEquals(offset + i, subarray.elementAt(i));
            i++;
        }
    }

    @Test
    public void iterator()
    {
        final ArrayWrapper array = createFilledArray();

        final Iterator<Long> iter = array.iterator();

        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(count1, iter.next(), 0);
            count1++;
        }
        assertEquals(LENGTH, count1);

        final Iterator<Long> iter2 = factory.create(0).iterator();

        int count2 = 0;
        while (iter2.hasNext())
        {
            iter2.next();
            count2++;
        }
        assertEquals(0, count2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBegin1()
    {
        final ArrayWrapper array = factory.create(LENGTH);
        array.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBegin2()
    {
        final ArrayWrapper array = factory.create(LENGTH);
        array.subRange(LENGTH + 1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBeginLength()
    {
        final ArrayWrapper array = factory.create(LENGTH);

        // ask for subrange that includes one-past-last element
        array.subRange(LENGTH/2, (LENGTH + 1)/2 + 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionIterator()
    {
        final ArrayWrapper array = factory.create(LENGTH);
        final Iterator<Long> iter = array.iterator();
        iter.remove();
    }

    @Test
    public void testEquals()
    {
        final ArrayWrapper array1 = createFilledArray();
        final ArrayWrapper array2 = createFilledArray();

        assertTrue(array1.equals(array1));

        assertTrue(array1.equals(array2));

        assertFalse(array1.equals(factory.create(0)));
        assertFalse(array1.equals(null));
        assertFalse(array1.equals(factory.create(1)));
    }

    protected ArrayWrapper createFilledArray()
    {
        final ArrayWrapper array = factory.create(LENGTH);

        for (int i = 0; i < LENGTH; i++)
        {
            array.setElementAt(i, i);
        }

        return array;
    }

    protected final ArrayFactory factory;

    // needs to fit all numeric arrays that are tested
    private final static long[] TEST_DATA = new long[] { 1, 2, 3, 4, 5 };
    private final static int LENGTH = 64; // must fit in a Byte with MSB cleared
}
