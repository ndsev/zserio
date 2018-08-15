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

public class Float32ArrayTest
{
    @Before
    public void setUp()
    {
        float32Array = new Float32Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            float32Array.setElementAt(1.0f, i);
        }
    }

    @Test
    public void testFloat32ArrayInt()
    {
        assertEquals(1024, float32Array.length());
        float32Array = new Float32Array(5);
        assertEquals(5, float32Array.length());
        float32Array = new Float32Array(20);
        assertEquals(20, float32Array.length());
    }

    @Test
    public void testFloat32ArrayFloat32ArrayIntInt()
    {
        final float[] data = new float[] {2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
        float32Array = new Float32Array(data, 0, 5);
        assertEquals(5, float32Array.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(2.0f, float32Array.elementAt(i), 0);
        }
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0f, float32Array.elementAt(i), 0);
        }
        for (int i = 0; i < 1024; i++)
        {
            float32Array.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, float32Array.elementAt(i), 0);
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            float32Array.setElementAt(2.0f, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(2.0f, float32Array.elementAt(i), 0);
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, float32Array.length());
        assertEquals(0, new Float32Array(0).length());
        float32Array = new Float32Array(1023);
        assertEquals(1023, float32Array.length());
    }

    @Test
    public void sum() throws Exception
    {
        final float expectedSum = 1024 * 1.0f;
        assertEquals(expectedSum, float32Array.sum(), Float.MIN_VALUE);
    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(32768, float32Array.bitSizeOf(0));
        assertEquals(32, new Float32Array(1).bitSizeOf(0));
        assertEquals(0, new Float32Array(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final Float32Array tmpArray = (Float32Array)float32Array.subRange(1000, 24);
        assertEquals(24, tmpArray.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(1.0f, tmpArray.elementAt(i), 0);
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        float32Array.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        float32Array.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        float32Array.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<Float> iter = float32Array.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(1.0f, iter.next(), 0);
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<Float> iter2 = new Float32Array(0).iterator();
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
        final Iterator<Float> iter = float32Array.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        float32Array.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(1.0f, in.readFloat32(), 0);
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
        float32Array = new Float32Array(in, 1);
        assertEquals(1, float32Array.length());
        assertEquals(1.0f, float32Array.elementAt(0), 0);
    }

    @Test
    public void testHashCode()
    {
        assertEquals(new Float32Array(0).hashCode(), new Float32Array(0).hashCode());
        assertTrue((new Float32Array(0).equals(new Float32Array(0))));
    }

    @Test
    public void testEquals()
    {
        final Float32Array tmpFloat32Array = new Float32Array(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpFloat32Array.setElementAt(1.0f, i);
        }
        assertTrue(float32Array.equals(float32Array));
        assertTrue(float32Array.equals(tmpFloat32Array));
        assertFalse(float32Array.equals(new Float32Array(0)));
        assertFalse(float32Array.equals(null));
        assertFalse(float32Array.equals(Integer.valueOf(1)));
    }

    private Float32Array float32Array;
}
