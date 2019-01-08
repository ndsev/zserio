package choice_types;

import static org.junit.Assert.*;

import choice_types.choice_with_array.TestChoice;
import choice_types.choice_with_array.Data8;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.array.ShortArray;

import org.junit.Test;

public class ChoiceWithArrayTest
{
    @Test
    public void array8()
    {
        final TestChoice testChoice = new TestChoice((byte)8);
        testChoice.setArray8(new ObjectArray<Data8>(4));

        // we just need to testChoice that getter for ObjectArray<?> doesn't fire a warning
        assertEquals(4, testChoice.getArray8().length());
    }

    @Test
    public void array16()
    {
        final TestChoice testChoice = new TestChoice((byte)16);
        testChoice.setArray16(new ShortArray(4));

        // we just need to testChoice that getter for ShortArray doesn't fire a warning
        assertEquals(4, testChoice.getArray16().length());
    }
}
