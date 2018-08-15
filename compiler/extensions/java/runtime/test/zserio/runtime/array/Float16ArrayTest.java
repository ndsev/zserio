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

public class Float16ArrayTest
{
    @Before
    public void setUp()
    {
        float16Array = new Float16Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            float16Array.setElementAt(1.0f, i);
        }
    }

    @Test
    public void testFloat16ArrayInt()
    {
        assertEquals(1024, float16Array.length());
        float16Array = new Float16Array(5);
        assertEquals(5, float16Array.length());
        float16Array = new Float16Array(20);
        assertEquals(20, float16Array.length());
    }

    @Test
    public void testFloat16ArrayFloat16ArrayIntInt()
    {
        final float[] data = new float[] {2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
        float16Array = new Float16Array(data, 0, 5);
        assertEquals(5, float16Array.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(2.0f, float16Array.elementAt(i), 0);
        }
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0f, float16Array.elementAt(i), 0);
        }
        for (int i = 0; i < 1024; i++)
        {
            float16Array.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, float16Array.elementAt(i), 0);
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            float16Array.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, float16Array.elementAt(i), 0);
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, float16Array.length());
        assertEquals(0, new Float16Array(0).length());
        float16Array = new Float16Array(1023);
        assertEquals(1023, float16Array.length());
    }

    @Test
    public void sum() throws Exception
    {
        final float expectedSum = 1024 * 1.0f;
        assertEquals(expectedSum, float16Array.sum(), Float.MIN_VALUE);
    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(16384, float16Array.bitSizeOf(0));
        assertEquals(16, new Float16Array(1).bitSizeOf(0));
        assertEquals(0, new Float16Array(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final Float16Array tmpArray = (Float16Array)float16Array.subRange(1000, 24);
        assertEquals(24, tmpArray.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(1.0f, tmpArray.elementAt(i), 0);
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        float16Array.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        float16Array.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        float16Array.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<Float> iter = float16Array.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(1.0f, iter.next(), 0);
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<Float> iter2 = new Float16Array(0).iterator();
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
        final Iterator<Float> iter = float16Array.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        float16Array.write(out);
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
        float16Array = new Float16Array(in, 1);
        assertEquals(1, float16Array.length());
        /*
         * Compare with 1.875 because of the float writing instead of float16.
         */
        assertEquals(1.875f, float16Array.elementAt(0), 0);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(new Float16Array(0).hashCode(), new Float16Array(0).hashCode());
        assertTrue((new Float16Array(0).equals(new Float16Array(0))));
    }

    @Test
    public void testEquals()
    {
        final Float16Array tmpFloat16Array = new Float16Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpFloat16Array.setElementAt(1.0f, i);
        }
        assertTrue(float16Array.equals(float16Array));
        assertTrue(float16Array.equals(tmpFloat16Array));
        assertFalse(float16Array.equals(new Float16Array(0)));
        assertFalse(float16Array.equals(null));
        assertFalse(float16Array.equals(Integer.valueOf(1)));
    }

    private Float16Array float16Array;
}
