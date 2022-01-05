package optional_members;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import optional_members.optional_array.TestStruct;
import optional_members.optional_array.Data8;

public class OptionalArrayTest
{
    @Test
    public void data8()
    {
        final TestStruct test = new TestStruct();
        test.setHasData8(true);
        test.setData8(new Data8[4]);
        assertEquals(4, test.getData8().length);
    }

    @Test
    public void autoData8()
    {
        final TestStruct test = new TestStruct();
        assertFalse(test.isAutoData8Used());
        test.setAutoData8(new Data8[4]);
        assertTrue(test.isAutoData8Used());
        assertEquals(4, test.getAutoData8().length);
    }

    @Test
    public void data16()
    {
        final TestStruct test = new TestStruct();
        test.setHasData8(false);
        test.setData16(new short[4]);
        assertEquals(4, test.getData16().length);
    }

    @Test
    public void autoData16()
    {
        final TestStruct test = new TestStruct();
        assertFalse(test.isAutoData16Used());
        test.setAutoData16(new short[4]);
        assertTrue(test.isAutoData16Used());
        assertEquals(4, test.getAutoData16().length);
    }
}
