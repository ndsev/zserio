package union_types;

import static org.junit.Assert.*;

import union_types.union_with_array.TestUnion;
import union_types.union_with_array.Data8;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.array.ShortArray;

import org.junit.Test;

public class UnionWithArrayTest
{
    @Test
    public void array8()
    {
        final TestUnion test = new TestUnion();
        test.setArray8(new ObjectArray<Data8>(4));

        // we just need to test that getter for ObjectArray<?> doesn't fire a warning
        assertEquals(4, test.getArray8().length());
    }

    @Test
    public void array16()
    {
        final TestUnion test = new TestUnion();
        test.setArray16(new ShortArray(4));

        // we just need to test that getter for ShortArray doesn't fire a warning
        assertEquals(4, test.getArray16().length());
    }
}
