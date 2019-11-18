package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BitBufferArrayTest
{
    @Before
    public void setUp()
    {
        bitBufferArray = new BitBufferArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            bitBufferArray.setElementAt(new BitBuffer(new byte[i], elementBitSize(i)), i);
        }
    }

    @Test
    public void testBitBufferArrayInt()
    {
        assertEquals(1024, bitBufferArray.length());
        bitBufferArray = new BitBufferArray(0);
        assertEquals(0, bitBufferArray.length());
        bitBufferArray = new BitBufferArray(1023);
        assertEquals(1023, bitBufferArray.length());
    }

    @Test
    public void testBitBufferArrayIntInt()
    {
        final BitBuffer[] data = new BitBuffer[] {new BitBuffer(new byte[1]), new BitBuffer(new byte[2]),
                new BitBuffer(new byte[3]), new BitBuffer(new byte[4]), new BitBuffer(new byte[5])};
        bitBufferArray = new BitBufferArray(data, 0, 5);
        assertEquals(5, bitBufferArray.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(new BitBuffer(new byte[i + 1]), bitBufferArray.elementAt(i));
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(new BitBuffer(new byte[i], elementBitSize(i)), bitBufferArray.elementAt(i));
        }
        for (int i = 0; i < 1024; i++)
        {
            bitBufferArray.setElementAt(new BitBuffer(new byte[1]), i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(new BitBuffer(new byte[1]), bitBufferArray.elementAt(i));
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, bitBufferArray.length());
        assertEquals(0, new BitBufferArray(0).length());
        assertEquals(1023, new BitBufferArray(1023).length());
    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(4202880, bitBufferArray.bitSizeOf(0));
        assertEquals(0, new BitBufferArray(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final BitBufferArray tmp = (BitBufferArray)bitBufferArray.subRange(1000, 24);
        assertEquals(24, tmp.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(new BitBuffer(new byte[i + 1000], elementBitSize(i + 1000)), tmp.elementAt(i));
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        bitBufferArray.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        bitBufferArray.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        bitBufferArray.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<BitBuffer> iter = bitBufferArray.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(new BitBuffer(new byte[count1], elementBitSize(count1)), iter.next());
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<BitBuffer> iter2 = new BitBufferArray(0).iterator();
        int count2 = 0;
        while (iter2.hasNext())
        {
            iter2.next();
            count2++;
        }
        assertEquals(0, count2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExceptionIterator()
    {
        final Iterator<BitBuffer> iter = bitBufferArray.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        bitBufferArray.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(new BitBuffer(new byte[i], elementBitSize(i)), in.readBitBuffer());
        }
    }

    @Test
    public void testHashCode()
    {
        assertEquals(1897556535, bitBufferArray.hashCode());
        assertEquals(new BitBufferArray(0).hashCode(), new BitBufferArray(0).hashCode());
        assertTrue((new BitBufferArray(0).equals(new BitBufferArray(0))));
    }

    @Test
    public void testEquals()
    {
        final BitBufferArray tmpBitBufferArray = new BitBufferArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpBitBufferArray.setElementAt(new BitBuffer(new byte[i], elementBitSize(i)), i);
        }
        assertTrue(bitBufferArray.equals(bitBufferArray));
        assertTrue(bitBufferArray.equals(tmpBitBufferArray));
        assertFalse(bitBufferArray.equals(new BitBufferArray(0)));
        assertFalse(bitBufferArray.equals(null));
        assertFalse(bitBufferArray.equals(Integer.valueOf(1)));
    }

    private long elementBitSize(int elementIndex)
    {
        return (long)elementIndex * 8 - (elementIndex % 8);
    }

    private BitBufferArray bitBufferArray;
}
