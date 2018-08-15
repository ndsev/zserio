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

public class Float64ArrayTest
{
    @Before
    public void setUp()
    {
        float64Array = new Float64Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            float64Array.setElementAt(1.0f, i);
        }
    }

    @Test
    public void testFloat64ArrayInt()
    {
        assertEquals(1024, float64Array.length());
        float64Array = new Float64Array(5);
        assertEquals(5, float64Array.length());
        float64Array = new Float64Array(20);
        assertEquals(20, float64Array.length());
    }

    @Test
    public void testFloat64ArrayFloat64ArrayIntInt()
    {
        final double[] data = new double[] {2.0, 2.0, 2.0, 2.0, 2.0};
        float64Array = new Float64Array(data, 0, 5);
        assertEquals(5, float64Array.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(2.0, float64Array.elementAt(i), 0);
        }
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0, float64Array.elementAt(i), 0);
        }
        for (int i = 0; i < 1024; i++)
        {
            float64Array.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0, float64Array.elementAt(i), 0);
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            float64Array.setElementAt(2.0, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0, float64Array.elementAt(i), 0);
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, float64Array.length());
        assertEquals(0, new Float64Array(0).length());
        float64Array = new Float64Array(1023);
        assertEquals(1023, float64Array.length());
    }

    @Test
    public void sum() throws Exception
    {
        final float expectedSum = 1024 * 1.0f;
        assertEquals(expectedSum, float64Array.sum(), Float.MIN_VALUE);
    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(65536, float64Array.bitSizeOf(0));
        assertEquals(64, new Float64Array(1).bitSizeOf(0));
        assertEquals(0, new Float64Array(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final Float64Array tmpArray = (Float64Array)float64Array.subRange(1000, 24);
        assertEquals(24, tmpArray.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(1.0, tmpArray.elementAt(i), 0);
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        float64Array.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        float64Array.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        float64Array.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<Double> iter = float64Array.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(1.0, iter.next(), 0);
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<Double> iter2 = new Float64Array(0).iterator();
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
        final Iterator<Double> iter = float64Array.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        float64Array.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0, in.readFloat64(), 0);
        }
    }

    @Test
    public void testConstructor() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeDouble(1.0);
        os.close();
        baos.close();
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());
        float64Array = new Float64Array(in, 1);
        assertEquals(1, float64Array.length());
        assertEquals(1.0, float64Array.elementAt(0), 0);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(new Float64Array(0).hashCode(), new Float64Array(0).hashCode());
        assertTrue((new Float64Array(0).equals(new Float64Array(0))));
    }

    @Test
    public void testEquals()
    {
        final Float64Array tmpFloat64Array = new Float64Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpFloat64Array.setElementAt(1.0, i);
        }
        assertTrue(float64Array.equals(float64Array));
        assertTrue(float64Array.equals(tmpFloat64Array));
        assertFalse(float64Array.equals(new Float64Array(0)));
        assertFalse(float64Array.equals(null));
        assertFalse(float64Array.equals(Integer.valueOf(1)));
    }

    private Float64Array float64Array;
}
