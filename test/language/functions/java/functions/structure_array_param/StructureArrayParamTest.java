package functions.structure_array_param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureArrayParamTest
{
    @Test
    public void checkParentStructure() throws IOException
    {
        final ParentStructure parentStructure = createParentStructure();
        assertEquals(CHILD_BIT_SIZE, parentStructure.funcGetChildBitSize());

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        parentStructure.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final byte[] expectedByteArray = writeParentStructureToByteArray();
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final ParentStructure readParentStructure = new ParentStructure(reader);
        assertEquals(parentStructure, readParentStructure);
    }

    private byte[] writeParentStructureToByteArray() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(NUM_CHILDREN, 8);

        for (ChildStructure childStructure : CHILDREN)
        {
            writer.writeBigInteger(childStructure.getValue(), childStructure.getBitSize());
        }

        writer.close();

        return writer.toByteArray();
    }

    private ParentStructure createParentStructure()
    {
        final ParentStructure parentStructure = new ParentStructure();

        parentStructure.setNumChildren(NUM_CHILDREN);
        parentStructure.setChildren(CHILDREN);

        return parentStructure;
    }

    private static final short CHILD_BIT_SIZE = 19;

    private static final ChildStructure[] CHILDREN = new ChildStructure[] {
            new ChildStructure(CHILD_BIT_SIZE, BigInteger.valueOf(0xAABB)),
            new ChildStructure(CHILD_BIT_SIZE, BigInteger.valueOf(0xCCDD))
    };

    private static final short NUM_CHILDREN = (short)CHILDREN.length;
}
