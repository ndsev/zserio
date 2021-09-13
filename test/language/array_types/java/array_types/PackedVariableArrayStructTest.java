 package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import array_types.packed_variable_array_struct.PackedVariableArray;
import array_types.packed_variable_array_struct.TestBitmask;
import array_types.packed_variable_array_struct.TestChoice;
import array_types.packed_variable_array_struct.TestEnum;
import array_types.packed_variable_array_struct.TestPackedArray;
import array_types.packed_variable_array_struct.TestStructure;
import array_types.packed_variable_array_struct.TestUnion;
import array_types.packed_variable_array_struct.TestUnpackedArray;
import array_types.packed_variable_array_struct.Value32;

public class PackedVariableArrayStructTest
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH2);
    }

    @Test
    public void bitSizeOfLength3() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH3);
    }

    @Test
    public void bitSizeOfLength4() throws IOException, ZserioError
    {
        checkBitSizeOf(VARIABLE_ARRAY_LENGTH4);
    }

    @Test
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH2);
    }

    @Test
    public void writeReadLength3() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH3);
    }

    @Test
    public void writeReadLength4() throws IOException, ZserioError
    {
        checkWriteRead(VARIABLE_ARRAY_LENGTH4);
    }

    private void checkBitSizeOf(int numElements) throws IOException, ZserioError
    {
        final PackedVariableArray packedVariableArray = createPackedVariableArray(numElements);
        final int unpackedBitsizeOf = packedVariableArray.getTestUnpackedArray().bitSizeOf();
        final int packedBitsizeOf = packedVariableArray.getTestPackedArray().bitSizeOf();
        final double minCompressionRatio = 0.59;
        assertTrue("Unpacked array has " + unpackedBitsizeOf + " bits, packed array has " + packedBitsizeOf +
                " bits, " + "compression ratio is " + packedBitsizeOf * 100.0 / unpackedBitsizeOf + "%!",
                unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf);
    }

    private void checkWriteRead(int numElements) throws IOException, ZserioError
    {
        final PackedVariableArray packedVariableArray = createPackedVariableArray(numElements);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        packedVariableArray.write(writer);
        writer.close();

        final long writtenBitPosition = writer.getBitPosition();
        assertEquals(packedVariableArray.bitSizeOf(), writtenBitPosition);
        assertEquals(packedVariableArray.initializeOffsets(0), writtenBitPosition);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final PackedVariableArray readPackedVariableArray = new PackedVariableArray(reader);
        assertEquals(packedVariableArray, readPackedVariableArray);
    }

    private PackedVariableArray createPackedVariableArray(int numElements)
    {
        final TestStructure[] testStructureArray = createTestStructureArray(numElements);
        final TestUnpackedArray testUnpackedArray = new TestUnpackedArray(numElements, testStructureArray);
        final TestPackedArray testPackedArray = new TestPackedArray(numElements, testStructureArray);

        return new PackedVariableArray(numElements, testUnpackedArray, testPackedArray);
    }

    private TestStructure[] createTestStructureArray(int numElements)
    {
        final TestStructure[] testStructureArray = new TestStructure[numElements];
        for (int i = 0; i < numElements; ++i)
            testStructureArray[i] = createTestStructure(i);

        return testStructureArray;
    }

    private TestStructure createTestStructure(int index)
    {
        final String name = new String("name" + index);
        final BitBuffer data = new BitBuffer(new byte[] {(byte)0xCD, (byte)0xC0}, 10);

        final TestChoice testChoice = new TestChoice(index);
        if (index == 0 || index == 2 || index == 4)
            testChoice.setValue16(index);
        else if (index == 5)
            testChoice.setArray32(new long[] {index * 2, index * 2 + 1});
        else
            testChoice.setValue32(new Value32(index * 2));

        final TestUnion testUnion = new TestUnion();
        if ((index % 2) == 0)
            testUnion.setValue16(index);
        else if (index == 5)
            testUnion.setArray32(new long[] {index *2, index * 2 + 1});
        else
            testUnion.setValue32(new Value32(index * 2));

        final TestEnum testEnum = ((index % 2) == 0) ? TestEnum.DARK_RED : TestEnum.DARK_GREEN;
        final TestBitmask testBitmask = ((index % 2) == 0) ? TestBitmask.Values.READ :
                TestBitmask.Values.CREATE;

        final Short testOptional = ((index % 2) == 0) ? (short)index : null;
        final BigInteger testDynamicBitfield = BigInteger.valueOf(index % 3);

        final BigInteger values[] = new BigInteger[] {
                BigInteger.valueOf(1),
                BigInteger.valueOf(4),
                BigInteger.valueOf(7),
                BigInteger.valueOf(10),
                BigInteger.valueOf(13),
                BigInteger.valueOf(16)};

        return new TestStructure(index, name, data, testChoice, testUnion, testEnum, testBitmask, testOptional,
                testDynamicBitfield, values.length, values, values);
    }

    private static final int VARIABLE_ARRAY_LENGTH1 = 25;
    private static final int VARIABLE_ARRAY_LENGTH2 = 50;
    private static final int VARIABLE_ARRAY_LENGTH3 = 100;
    private static final int VARIABLE_ARRAY_LENGTH4 = 1000;
}
