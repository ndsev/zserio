package optional_members;

import static org.junit.Assert.*;

import org.junit.Test;

import optional_members.optional_array.TestStruct;
import optional_members.optional_array.Data8;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.array.ShortArray;

public class OptionalArrayTest
{
    @Test
    public void data8()
    {
        final TestStruct test = new TestStruct();
        test.setHasData8(true);
        test.setData8(new ObjectArray<Data8>(4));

        // we just need to test that getter for ObjectArray<?> doesn't fire a warning
        assertEquals(4, test.getData8().length());
    }

    @Test
    public void autoData8()
    {
        final TestStruct test = new TestStruct();
        assertFalse(test.isAutoData8Used());
        test.setAutoData8(new ObjectArray<Data8>(4));
        assertTrue(test.isAutoData8Used());

        // we just need to test that getter for ObjectArray<?> doesn't fire a warning
        assertEquals(4, test.getAutoData8().length());
    }

    @Test
    public void data16()
    {
        final TestStruct test = new TestStruct();
        test.setHasData8(false);
        test.setData16(new ShortArray(4));

        // we just need to test that getter for ShortArray doesn't fire a warning
        assertEquals(4, test.getData16().length());
    }

    @Test
    public void autoData16()
    {
        final TestStruct test = new TestStruct();
        assertFalse(test.isAutoData16Used());
        test.setAutoData16(new ShortArray(4));
        assertTrue(test.isAutoData16Used());

        // we just need to test that getter for ShortArray doesn't fire a warning
        assertEquals(4, test.getAutoData16().length());
    }
}
