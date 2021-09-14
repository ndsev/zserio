package union_types;

import static org.junit.Assert.*;

import union_types.union_with_array.TestUnion;
import union_types.union_with_array.Data8;

import org.junit.Test;

public class UnionWithArrayTest
{
    @Test
    public void array8()
    {
        final TestUnion test = new TestUnion();
        test.setArray8(new Data8[4]);
        assertEquals(4, test.getArray8().length);
    }

    @Test
    public void array16()
    {
        final TestUnion test = new TestUnion();
        test.setArray16(new short[4]);
        assertEquals(4, test.getArray16().length);
    }
}
