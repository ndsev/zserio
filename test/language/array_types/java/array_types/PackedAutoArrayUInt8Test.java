package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import array_types.packed_auto_array_uint8.PackedAutoArray;

public class PackedAutoArrayUInt8Test
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void bitSizeOfLength3() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH3);
    }

    @Test
    public void initializeOffsetsLength1() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void initializeOffsetsLength2() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void initializeOffsetsLength3() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH3);
    }

    @Test
    public void readLength1() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void readLength2() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void readLength3() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH3);
    }

    @Test
    public void writeLength1() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeLength2() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void writeLength3() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH2);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray(numElements);
        final int bitPosition = 2;
        final int autoArrayBitSize = calcPackedAutoArrayBitSize(numElements);
        assertEquals(autoArrayBitSize, packedAutoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray(numElements);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + calcPackedAutoArrayBitSize(numElements);
        assertEquals(expectedEndBitPosition, packedAutoArray.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writePackedAutoArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final PackedAutoArray packedAutoArray = new PackedAutoArray(stream);
        stream.close();

        checkPackedAutoArray(packedAutoArray, numElements);
    }

    private void checkWrite(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray(numElements);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        packedAutoArray.write(writer);
        writer.close();

        final PackedAutoArray readPackedAutoArray = new PackedAutoArray(file);
        checkPackedAutoArray(readPackedAutoArray, numElements);
    }

    private void checkPackedAutoArray(PackedAutoArray packedAutoArray, short expectedNumElements)
    {
        final short[] uint8Array = packedAutoArray.getUint8Array();
        assertEquals(expectedNumElements, uint8Array.length);
        short value = PACKED_ARRAY_ELEMENT0;
        assertEquals(value, uint8Array[0]);
        value += PACKED_ARRAY_DELTA;
        for (short i = 1; i < expectedNumElements; ++i)
        {
            value += PACKED_ARRAY_DELTA;
            assertEquals(value, uint8Array[i]);
        }
    }

    private PackedAutoArray createPackedAutoArray(short numElements)
    {
        final short[] uint8Array = new short[numElements];
        short value = PACKED_ARRAY_ELEMENT0;
        uint8Array[0] = value;
        value += PACKED_ARRAY_DELTA;
        for (short i = 1; i < numElements; ++i)
        {
            value += PACKED_ARRAY_DELTA;
            uint8Array[i] = value;
        }

        return new PackedAutoArray(uint8Array);
    }

    private void writePackedAutoArrayToFile(File file, short numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeVarSize(numElements);
        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        short value = PACKED_ARRAY_ELEMENT0;
        writer.writeBits(value, 8);
        if (numElements > 1)
        {
            writer.writeSignedBits(PACKED_ARRAY_DELTA * 2, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
            for (short i = 2; i < numElements; ++i)
                writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
        }

        writer.close();
    }

    private int calcPackedAutoArrayBitSize(short numElements)
    {
        int bitSize = 8; // auto array size: varsize
        bitSize += 1; // packing descriptor: is_packed
        if (numElements > 1)
            bitSize += 6; // packing descriptor: max_bit_number
        bitSize += 8; // first element
        bitSize += (numElements - 1) * (PACKED_ARRAY_MAX_BIT_NUMBER + 1); // all deltas

        return bitSize;
    }

    private static final short AUTO_ARRAY_LENGTH1 = 1;
    private static final short AUTO_ARRAY_LENGTH2 = 5;
    private static final short AUTO_ARRAY_LENGTH3 = 10;

    private static final short PACKED_ARRAY_ELEMENT0 = 255;
    private static final short PACKED_ARRAY_DELTA = -2;
    private static final int PACKED_ARRAY_MAX_BIT_NUMBER = 3;
}