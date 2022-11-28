package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import array_types.variable_array_subtyped_struct.TestStructure;
import array_types.variable_array_subtyped_struct.VariableArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class VariableArraySubtypedStructTest
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final short numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        final VariableArray variableArray = new VariableArray(numElements, compoundArray);
        final int bitPosition = 2;
        final int numOneNumberIndexes = 10;
        final int expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
        assertEquals(expectedBitSize, variableArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final short numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        final VariableArray variableArray = new VariableArray(numElements, compoundArray);
        final int bitPosition = 2;
        final int numOneNumberIndexes = 10;
        final int expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
        assertEquals(expectedEndBitPosition, variableArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final short numElements = 59;
        final BitBuffer buffer = writeSubtypedStructVariableArrayToBitBuffer(numElements);
        final VariableArray variableArray = SerializeUtil.deserialize(VariableArray.class, buffer);

        assertEquals(numElements, variableArray.getNumElements());
        final TestStructure[] compoundArray = variableArray.getCompoundArray();
        assertEquals(numElements, compoundArray.length);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = compoundArray[i];
            assertEquals(i, testStructure.getId());
            assertTrue(testStructure.getName().equals("Name" + i));
        }
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final short numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        final VariableArray variableArray = new VariableArray(numElements, compoundArray);
        final File file = new File(BLOB_NAME);
        SerializeUtil.serializeToFile(variableArray, file);

        final VariableArray readVariableArray = SerializeUtil.deserializeFromFile(VariableArray.class, file);
        assertEquals(numElements, readVariableArray.getNumElements());
        final TestStructure[] readCompoundArray = readVariableArray.getCompoundArray();
        assertEquals(numElements, readCompoundArray.length);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure readTestStructure = readCompoundArray[i];
            assertEquals(i, readTestStructure.getId());
            assertTrue(readTestStructure.getName().equals("Name" + i));
        }
    }

    @Test
    public void writeWrongArray() throws IOException, ZserioError
    {
        final short numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        VariableArray variableArray = new VariableArray((short)(numElements + 1), compoundArray);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> variableArray.write(writer));
        writer.close();
    }

    private BitBuffer writeSubtypedStructVariableArrayToBitBuffer(short numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeSignedBits(numElements, 8);
            for (short i = 0; i < numElements; ++i)
            {
                writer.writeUnsignedInt(i);
                writer.writeString("Name" + i);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "variable_array_subtyped_struct.blob";
}
