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

public class StringArrayTest
{
    @Before
    public void setUp()
    {
        stringArray = new StringArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            stringArray.setElementAt(Integer.toString(i), i);
        }
    }

    @Test
    public void testStringArrayInt()
    {
        assertEquals(1024, stringArray.length());
        stringArray = new StringArray(0);
        assertEquals(0, stringArray.length());
        stringArray = new StringArray(1023);
        assertEquals(1023, stringArray.length());
    }

    @Test
    public void testStringArrayStringArrayIntInt()
    {
        final String[] data = new String[] {"1", "2", "3", "4", "5"};
        stringArray = new StringArray(data, 0, 5);
        assertEquals(5, stringArray.length());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(Integer.toString(i + 1), stringArray.elementAt(i));
        }
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(Integer.toString(i), stringArray.elementAt(i));
        }
        for (int i = 0; i < 1024; i++)
        {
            stringArray.setElementAt(Integer.toString(1), i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(Integer.toString(1), stringArray.elementAt(i));
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            stringArray.setElementAt("1", i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals("1", stringArray.elementAt(i));
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, stringArray.length());
        assertEquals(0, new StringArray(0).length());
        assertEquals(1023, new StringArray(1023).length());

    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(32080, stringArray.bitSizeOf(0));
        assertEquals(0, new StringArray(0).bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final StringArray tmp = (StringArray)stringArray.subRange(1000, 24);
        assertEquals(24, tmp.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(Integer.toString(i + 1000), tmp.elementAt(i));
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin1()
    {
        stringArray.subRange(-1, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBegin2()
    {
        stringArray.subRange(1025, 24);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSubRangeExceptionBeginLength()
    {
        stringArray.subRange(1000, 25);
    }

    @Test
    public void testIterator()
    {
        final Iterator<String> iter = stringArray.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(Integer.toString(count1), iter.next());
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<String> iter2 = new StringArray(0).iterator();
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
        final Iterator<String> iter = stringArray.iterator();
        iter.remove();
    }

    @Test
    public void testWrite() throws IOException
    {
        final ByteArrayBitStreamWriter out = new ByteArrayBitStreamWriter();
        stringArray.write(out);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(out.toByteArray());
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(Integer.toString(i), in.readString());
        }
    }

    @Test
    public void testConstructor() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos);
        os.writeUTF("");
        os.close();
        baos.close();
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray());
        stringArray = new StringArray(in, 1);
        assertEquals(1, stringArray.length());
        assertEquals("", stringArray.elementAt(0));
    }

    @Test
    public void testHashCode()
    {
        assertEquals(-1995349915, stringArray.hashCode());
        assertEquals(new FloatArray(0).hashCode(), new FloatArray(0).hashCode());
        assertTrue((new FloatArray(0).equals(new FloatArray(0))));
    }

    @Test
    public void testEquals()
    {
        final StringArray tmpStringArray = new StringArray(1024);
        for (int i = 0; i < 1024; i++)
        {
            tmpStringArray.setElementAt(Integer.toString(i), i);
        }
        assertTrue(stringArray.equals(stringArray));
        assertTrue(stringArray.equals(tmpStringArray));
        assertFalse(stringArray.equals(new StringArray(0)));
        assertFalse(stringArray.equals(null));
        assertFalse(stringArray.equals(Integer.valueOf(1)));
    }

    private StringArray stringArray;
}
