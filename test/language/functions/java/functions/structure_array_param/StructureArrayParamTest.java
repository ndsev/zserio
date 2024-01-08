package functions.structure_array_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureArrayParamTest
{
    @Test
    public void checkParentStructure() throws IOException
    {
        final ParentStructure parentStructure = createParentStructure();
        assertEquals(CHILD_BIT_SIZE, parentStructure.funcGetChildBitSize());
        assertEquals(ANOTHER_CHILD_BIT_SIZE, parentStructure.getNotLeftMost().funcGetAnotherChildBitSize());

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        parentStructure.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();

        final byte[] expectedByteArray = writeParentStructureToByteArray();
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writtenByteArray, writer.getBitPosition());
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

        writer.writeBits(NUM_ANOTHER_CHILDREN, 8);
        for (ChildStructure childStructure : ANOTHER_CHILDREN)
        {
            writer.writeBigInteger(childStructure.getValue(), childStructure.getBitSize());
        }

        writer.close();

        return writer.toByteArray();
    }

    private ParentStructure createParentStructure()
    {
        final ParentStructure parentStructure = new ParentStructure();

        parentStructure.setNotLeftMost(new NotLeftMost());
        parentStructure.setNumChildren(NUM_CHILDREN);
        parentStructure.setChildren(CHILDREN);
        parentStructure.setNumAnotherChildren(NUM_ANOTHER_CHILDREN);
        parentStructure.setAnotherChildren(ANOTHER_CHILDREN);

        return parentStructure;
    }

    private static final short CHILD_BIT_SIZE = 19;
    private static final short ANOTHER_CHILD_BIT_SIZE = 17;

    private static final ChildStructure[] CHILDREN = new ChildStructure[] {
            new ChildStructure(CHILD_BIT_SIZE, BigInteger.valueOf(0xAABB)),
            new ChildStructure(CHILD_BIT_SIZE, BigInteger.valueOf(0xCCDD))};

    private static final ChildStructure[] ANOTHER_CHILDREN = new ChildStructure[] {
            new ChildStructure(ANOTHER_CHILD_BIT_SIZE, BigInteger.valueOf(0xAABB)),
            new ChildStructure(ANOTHER_CHILD_BIT_SIZE, BigInteger.valueOf(0xCCDD))};

    private static final short NUM_CHILDREN = (short)CHILDREN.length;
    private static final short NUM_ANOTHER_CHILDREN = (short)ANOTHER_CHILDREN.length;
}
