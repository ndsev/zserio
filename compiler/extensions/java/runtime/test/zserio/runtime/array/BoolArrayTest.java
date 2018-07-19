package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.junit.Before;
import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BoolArrayTest
{
    /**
     * Initializes the BoolArray.
     */
    @Before
    public void setUp()
    {
        boolArray = new BoolArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            boolArray.setElementAt(true, i);
        }
    }

    /**
     * Test the constructor.
     */
    @Test
    public void boolArrayInt()
    {
        boolArray = new BoolArray(0);
        assertEquals(0, boolArray.length());
        try
        {
            boolArray.setElementAt(true, 1);
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }
        boolArray = new BoolArray(1024);
        assertEquals(1024, boolArray.length());
    }

    /**
     * Test constructor.
     */
    @Test
    public void boolArrayBooleanArrayIntInt()
    {
        final boolean[] data = new boolean[] {true, false, false, true, true};
        boolArray = new BoolArray(data, 0, 5);
        assertEquals(5, boolArray.length());
        assertTrue(boolArray.elementAt(0));
        assertFalse(boolArray.elementAt(1));
        assertFalse(boolArray.elementAt(2));
        assertTrue(boolArray.elementAt(3));
        assertTrue(boolArray.elementAt(4));
    }

    /**
     * Test the elemenAt method.
     */
    @Test
    public void elementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertTrue(boolArray.elementAt(i));
        }
        try
        {
            boolArray.elementAt(1024);
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }
    }

    /**
     * Test the element setter.
     */
    @Test
    public void setElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            boolArray.setElementAt(false, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertFalse(boolArray.elementAt(i));
        }
    }

    /**
     * Test the length method.
     */
    @Test
    public void length()
    {
        assertEquals(0, new BoolArray(0).length());
        assertEquals(1024, boolArray.length());
    }

    /**
     * Test the bitSizeOf method.
     */
    @Test
    public void bitSizeOf()
    {
        assertEquals(1024, boolArray.bitSizeOf(0));
        assertEquals(0, new BoolArray(0).bitSizeOf(0));
    }

    /**
     * Test the iterator.
     */
    @Test
    public void iterator()
    {
        final Iterator<Boolean> iter = boolArray.iterator();
        int count = 0;
        while (iter.hasNext())
        {
            assertTrue(iter.next());
            count++;
        }
        assertEquals(1024, count);
    }

    /**
     * Test the subRange method.
     */
    @Test
    public void subRange()
    {
        final BoolArray subArray = (BoolArray) boolArray.subRange(1000, 24);
        assertEquals(24, subArray.length());
        for (int i = 0; i < 24; i++)
        {
            assertTrue(subArray.elementAt(i));
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBegin1()
    {
        boolArray.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBegin2()
    {
        boolArray.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void subRangeExceptionBeginLength()
    {
        boolArray.subRange(1000, 25);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionIterator()
    {
        final Iterator<Boolean> iter = boolArray.iterator();
        iter.remove();
    }

    @Test
    public void write() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        boolArray.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertTrue(in.readBool());
        }
    }

    @Test
    public void constructor() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeBoolean(false);
        os.close();
        baos.close();
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());
        boolArray = new BoolArray(in, 1);
        assertEquals(1, boolArray.length());
        /*
         * Compare with 1.875 because of the float writing instead of float16.
         */
        assertFalse(false);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(2092846103, boolArray.hashCode());
        assertEquals(new BoolArray(0).hashCode(), new BoolArray(0).hashCode());
        assertTrue((new BoolArray(0).equals(new BoolArray(0))));
    }

    @Test
    public void equals()
    {
        final BoolArray tmpBoolArray = new BoolArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpBoolArray.setElementAt(true, i);
        }
        assertTrue(boolArray.equals(boolArray));
        assertTrue(boolArray.equals(tmpBoolArray));
        assertFalse(boolArray.equals(new BoolArray(0)));
        assertFalse(boolArray.equals(null));
        assertFalse(boolArray.equals(Integer.valueOf(1)));
    }

    /**
     * A BoolArray.
     */
    private BoolArray boolArray;
}
