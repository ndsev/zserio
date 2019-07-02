package functions.structure_parent_child_value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureParentChildValueTest
{
    @Test
    public void checkParentValue() throws IOException
    {
        final ParentValue parentValue = createParentValue();
        assertEquals(CHILD_VALUE, parentValue.funcGetValue());

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        parentValue.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final byte[] expectedByteArray = writeParentValueToByteArray();
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final ParentValue readParentValue = new ParentValue(reader);
        assertEquals(parentValue, readParentValue);
    }

    private byte[] writeParentValueToByteArray() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(CHILD_VALUE, 32);
        writer.close();

        return writer.toByteArray();
    }

    private ParentValue createParentValue()
    {
        final ChildValue childValue = new ChildValue(CHILD_VALUE);

        return new ParentValue(childValue);
    }

    private static int CHILD_VALUE = 0xABCD;
}
