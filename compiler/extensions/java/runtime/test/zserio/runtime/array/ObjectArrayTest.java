package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ObjectArrayTest
{
    @Before
    public void setUp()
    {
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();

        for (int i = 0; i < 1024; i++)
        {
            data.add(new WriterImplementer(1));
        }
        objectArray = new ObjectArray<WriterImplementer>(data);
    }

    @Test
    public void testObjectArrayInt()
    {
        assertEquals(1024, objectArray.length());
        objectArray = new ObjectArray<WriterImplementer>(0);
        assertEquals(0, objectArray.length());
    }

    @Test
    public void testObjectArrayObjectArrayIntInt()
    {
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();
        data.add(new WriterImplementer(1));
        objectArray = new ObjectArray<WriterImplementer>(data);
        assertEquals(1, objectArray.length());
    }

    @Test
    public void testElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(new WriterImplementer(1), objectArray.elementAt(i));
        }
        for (int i = 0; i < 1024; i++)
        {
            objectArray.setElementAt(new WriterImplementer(2), i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertEquals(new WriterImplementer(2), objectArray.elementAt(i));
        }
    }

    @Test
    public void testSetElementAt()
    {
        for (int i = 0; i < 1024; i++)
        {
            objectArray.setElementAt(null, i);
        }
        for (int i = 0; i < 1024; i++)
        {
            assertNull(objectArray.elementAt(i));
        }
    }

    @Test
    public void testLength()
    {
        assertEquals(1024, objectArray.length());
        assertEquals(0, new ObjectArray<WriterImplementer>(new ArrayList<WriterImplementer>()).length());

    }

    @Test
    public void testBitsizeof()
    {
        assertEquals(Byte.SIZE * 1024, objectArray.bitSizeOf(0));
    }

    @Test
    public void testSubRange()
    {
        final ObjectArray<WriterImplementer> tmp =
                (ObjectArray<WriterImplementer>)objectArray.subRange(1000, 24);
        assertEquals(24, tmp.length());
        for (int i = 0; i < 24; i++)
        {
            assertEquals(new WriterImplementer(1), tmp.elementAt(i));
        }
    }

    @Test
    public void testIterator()
    {
        final Iterator<WriterImplementer> iter = objectArray.iterator();
        int count1 = 0;
        while (iter.hasNext())
        {
            assertEquals(new WriterImplementer(1), iter.next());
            count1++;
        }
        assertEquals(1024, count1);

        final Iterator<WriterImplementer> iter2 = new ObjectArray<WriterImplementer>(0).iterator();
        int count2 = 0;
        while (iter2.hasNext())
        {
            iter2.next();
            count2++;
        }
        assertEquals(0, count2);
    }

    @Test
    public void testObjectArrayListOfE()
    {
        assertEquals(1024, objectArray.length());
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();
        for (int i = 0; i < 1023; i++)
        {
            data.add(new WriterImplementer(2));
        }
        objectArray = new ObjectArray<WriterImplementer>(data);
        assertEquals(1023, objectArray.length());
    }

    @Test
    public void testGetData()
    {
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();

        for (int i = 0; i < 1024; i++)
        {
            data.add(new WriterImplementer(1));
        }
        assertEquals(data, objectArray.getData());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetElementException()
    {
        objectArray = new ObjectArray<WriterImplementer>(1);
        objectArray.setElementAt(new WriterImplementer(1), 1);
    }

    @Test
    public void testEquals()
    {
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();
        data.add(new WriterImplementer(1));
        objectArray = new ObjectArray<WriterImplementer>(data);
        final ObjectArray<WriterImplementer> tmpArray = new ObjectArray<WriterImplementer>(data);

        assertTrue(objectArray.equals(tmpArray));
        assertTrue(objectArray.equals(objectArray));
        assertFalse(objectArray.equals(null));
        assertFalse(objectArray.equals(Integer.valueOf(1)));
    }

    @Test
    public void testHashCode()
    {
        final List<WriterImplementer> data = new ArrayList<WriterImplementer>();
        data.add(new WriterImplementer(1));
        objectArray = new ObjectArray<WriterImplementer>(data);
        final ObjectArray<WriterImplementer> tmpArray = new ObjectArray<WriterImplementer>(data);

        assertEquals(objectArray.hashCode(), tmpArray.hashCode());
        assertEquals(63, tmpArray.hashCode());
    }

    private ObjectArray<WriterImplementer> objectArray;
}
