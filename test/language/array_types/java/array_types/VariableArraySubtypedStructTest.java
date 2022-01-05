package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import array_types.variable_array_subtyped_struct.TestStructure;
import array_types.variable_array_subtyped_struct.VariableArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
        final File file = new File("test.bin");
        writeSubtypedStructVariableArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final VariableArray variableArray = new VariableArray(stream);
        stream.close();

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
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        variableArray.write(writer);
        writer.close();

        assertEquals(variableArray.bitSizeOf(), writer.getBitPosition());
        assertEquals(variableArray.initializeOffsets(0), writer.getBitPosition());

        final VariableArray readVariableArray = new VariableArray(file);
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

        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> variableArray.write(writer));
        writer.close();
    }

    private void writeSubtypedStructVariableArrayToFile(File file, short numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeSignedBits(numElements, 8);
        for (short i = 0; i < numElements; ++i)
        {
            writer.writeUnsignedInt(i);
            writer.writeString("Name" + i);
        }

        writer.close();
    }

    private static final String BLOB_NAME = "variable_array_subtyped_struct.blob";
}
