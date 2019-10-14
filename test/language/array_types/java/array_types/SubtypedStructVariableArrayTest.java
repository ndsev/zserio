package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import array_types.subtyped_struct_variable_array.TestStructure;
import array_types.subtyped_struct_variable_array.SubtypedStructVariableArray;

import zserio.runtime.ZserioError;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class SubtypedStructVariableArrayTest
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final short numElements = 33;
        final ObjectArray<TestStructure> compoundArray = new ObjectArray<TestStructure>(numElements);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray.setElementAt(testStructure, i);
        }
        final SubtypedStructVariableArray subtypedStructVariableArray =
                new SubtypedStructVariableArray(numElements, compoundArray);
        final int bitPosition = 2;
        final int numOneNumberIndexes = 10;
        final int expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
        assertEquals(expectedBitSize, subtypedStructVariableArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final short numElements = 33;
        final ObjectArray<TestStructure> compoundArray = new ObjectArray<TestStructure>(numElements);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray.setElementAt(testStructure, i);
        }
        final SubtypedStructVariableArray subtypedStructVariableArray =
                new SubtypedStructVariableArray(numElements, compoundArray);
        final int bitPosition = 2;
        final int numOneNumberIndexes = 10;
        final int expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8;
        assertEquals(expectedEndBitPosition, subtypedStructVariableArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final short numElements = 59;
        final File file = new File("test.bin");
        writeSubtypedStructVariableArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final SubtypedStructVariableArray subtypedStructVariableArray = new SubtypedStructVariableArray(stream);
        stream.close();

        assertEquals(numElements, subtypedStructVariableArray.getNumElements());
        final ObjectArray<TestStructure> compoundArray = subtypedStructVariableArray.getCompoundArray();
        assertEquals(numElements, compoundArray.length());
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = compoundArray.elementAt(i);
            assertEquals(i, testStructure.getId());
            assertTrue(testStructure.getName().equals("Name" + i));
        }
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final short numElements = 33;
        final ObjectArray<TestStructure> compoundArray = new ObjectArray<TestStructure>(numElements);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray.setElementAt(testStructure, i);
        }
        final SubtypedStructVariableArray subtypedStructVariableArray =
                new SubtypedStructVariableArray(numElements, compoundArray);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        subtypedStructVariableArray.write(writer);
        writer.close();

        final SubtypedStructVariableArray readSubtypedStructVariableArray =
                new SubtypedStructVariableArray(file);
        assertEquals(numElements, readSubtypedStructVariableArray.getNumElements());
        final ObjectArray<TestStructure> readCompoundArray = readSubtypedStructVariableArray.getCompoundArray();
        assertEquals(numElements, readCompoundArray.length());
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure readTestStructure = readCompoundArray.elementAt(i);
            assertEquals(i, readTestStructure.getId());
            assertTrue(readTestStructure.getName().equals("Name" + i));
        }
    }

    @Test(expected=ZserioError.class)
    public void writeWrongArray() throws IOException, ZserioError
    {
        final short numElements = 33;
        ObjectArray<TestStructure> compoundArray = new ObjectArray<TestStructure>(numElements);
        for (short i = 0; i < numElements; ++i)
        {
            final TestStructure testStructure = new TestStructure(i, "Name" + i);
            compoundArray.setElementAt(testStructure, i);
        }
        SubtypedStructVariableArray subtypedStructVariableArray =
                new SubtypedStructVariableArray((short)(numElements + 1), compoundArray);

        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        subtypedStructVariableArray.write(writer);
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
}
