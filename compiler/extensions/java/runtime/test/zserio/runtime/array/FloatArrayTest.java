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

public class FloatArrayTest
{
    @Before
    public void setUp()
    {
        floatArray = new FloatArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            floatArray.setElementAt(1.0f, i);
        }
    }

    @Test
    public void testFloatArrayInt()
    {
        assertEquals(1024, floatArray.length());
        floatArray = new FloatArray(5);
        assertEquals(5, floatArray.length());
        floatArray = new FloatArray(20);
        assertEquals(20, floatArray.length());
    }

    @Test
    public void testFloatArrayFloatArrayIntInt()
    {
        final float[] data = new float[] {2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
        floatArray = new FloatArray(data, 0, 5);
        assertEquals(5, floatArray.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(2.0f, floatArray.elementAt(i), 0);
        }
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0f, floatArray.elementAt(i), 0);
        }
        for (int i = 0; i < 1024; i++)
        {
            floatArray.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, floatArray.elementAt(i), 0);
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            floatArray.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, floatArray.elementAt(i), 0);
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, floatArray.length());
        assertEquals(0, new FloatArray(0).length());
        floatArray = new FloatArray(1023);
        assertEquals(1023, floatArray.length());
    }

    @Test
    public void sum() throws Exception
    {
        final float expectedSum = 1024 * 1.0f;
        assertEquals(expectedSum, floatArray.sum(), Float.MIN_VALUE);
    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(16384, floatArray.bitSizeOf(0));
        assertEquals(16, new FloatArray(1).bitSizeOf(0));
        assertEquals(0, new FloatArray(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final FloatArray tmpArray = (FloatArray)floatArray.subRange(1000, 24);
        assertEquals(24, tmpArray.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(1.0f, tmpArray.elementAt(i), 0);
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        floatArray.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        floatArray.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        floatArray.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<Float> iter = floatArray.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(1.0f, iter.next(), 0);
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<Float> iter2 = new FloatArray(0).iterator();
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
        final Iterator<Float> iter = floatArray.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        floatArray.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0f, in.readFloat16(), 0);
        }
    }

    @Test
    public void testConstructor() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeFloat(1f);
        os.close();
        baos.close();
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());
        floatArray = new FloatArray(in, 1);
        assertEquals(1, floatArray.length());
        /*
         * Compare with 1.875 because of the float writing instead of float16.
         */
        assertEquals(1.875f, floatArray.elementAt(0), 0);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(17960983, floatArray.hashCode());
        assertEquals(new FloatArray(0).hashCode(), new FloatArray(0).hashCode());
        assertTrue((new FloatArray(0).equals(new FloatArray(0))));
    }

    @Test
    public void testEquals()
    {
        final FloatArray tmpFloatArray = new FloatArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpFloatArray.setElementAt(1.0f, i);
        }
        assertTrue(floatArray.equals(floatArray));
        assertTrue(floatArray.equals(tmpFloatArray));
        assertFalse(floatArray.equals(new FloatArray(0)));
        assertFalse(floatArray.equals(null));
        assertFalse(floatArray.equals(Integer.valueOf(1)));
    }

    /**
     * A float array.
     */
    private FloatArray floatArray;
}
