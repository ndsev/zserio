package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import array_types.variable_array_struct_cast_int8.TestStructure;
import array_types.variable_array_struct_cast_int8.VariableArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class VariableArrayStructCastInt8Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final byte numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (byte i = 0; i < numElements; ++i)
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
        final byte numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (byte i = 0; i < numElements; ++i)
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
        final byte numElements = 59;
        final File file = new File("test.bin");
        writeVariableArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final VariableArray variableArray = new VariableArray(stream);
        stream.close();

        assertEquals(numElements, variableArray.getNumElements());
        final TestStructure[] compoundArray = variableArray.getCompoundArray();
        assertEquals(numElements, compoundArray.length);
        for (byte i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = compoundArray[i];
            assertEquals(i, testStructure.getId());
            assertTrue(testStructure.getName().equals("Name" + i));
        }
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final byte numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        final VariableArray variableArray = new VariableArray(numElements, compoundArray);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        variableArray.write(writer);
        writer.close();

        final VariableArray readVariableArray = new VariableArray(file);
        assertEquals(numElements, readVariableArray.getNumElements());
        final TestStructure[] readCompoundArray = readVariableArray.getCompoundArray();
        assertEquals(numElements, readCompoundArray.length);
        for (byte i = 0; i < numElements; ++i)
        {
            final TestStructure readTestStructure = readCompoundArray[i];
            assertEquals(i, readTestStructure.getId());
            assertTrue(readTestStructure.getName().equals("Name" + i));
        }
    }

    @Test(expected=ZserioError.class)
    public void writeWrongArray() throws IOException, ZserioError
    {
        final byte numElements = 33;
        final TestStructure[] compoundArray = new TestStructure[numElements];
        for (byte i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray[i] = testStructure;
        }
        VariableArray variableArray = new VariableArray((byte)(numElements + 1), compoundArray);

        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        variableArray.write(writer);
        writer.close();
    }

    private void writeVariableArrayToFile(File file, byte numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeByte(numElements);
        for (byte i = 0; i < numElements; ++i)
        {
            writer.writeUnsignedInt(i);
            writer.writeString("Name" + i);
        }

        writer.close();
    }
}
