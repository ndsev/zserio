package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import array_types.packed_auto_array_struct_recursion.PackedAutoArrayRecursion;

public class PackedAutoArrayStructRecursionTest
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
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void writeReadLength3() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH3);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArrayRecursion packedAutoArrayRecursion = createPackedAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int autoArrayRecursionBitSize = calcPackedAutoArrayRecursionBitSize(numElements);
        assertEquals(autoArrayRecursionBitSize, packedAutoArrayRecursion.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArrayRecursion packedAutoArrayRecursion = createPackedAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + calcPackedAutoArrayRecursionBitSize(numElements);
        assertEquals(expectedEndBitPosition, packedAutoArrayRecursion.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final BitBuffer buffer = writePackedAutoArrayRecursionToBitBuffer(numElements);
        final PackedAutoArrayRecursion packedAutoArrayRecursion =
                SerializeUtil.deserialize(PackedAutoArrayRecursion.class, buffer);
        checkPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);
    }

    private void checkWriteRead(short numElements) throws IOException, ZserioError
    {
        final PackedAutoArrayRecursion packedAutoArrayRecursion = createPackedAutoArrayRecursion(numElements);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        SerializeUtil.serializeToFile(packedAutoArrayRecursion, file);
        final PackedAutoArrayRecursion readAutoArrayRecursion =
                SerializeUtil.deserializeFromFile(PackedAutoArrayRecursion.class, file);
        checkPackedAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    private PackedAutoArrayRecursion createPackedAutoArrayRecursion(short numElements)
    {
        final PackedAutoArrayRecursion[] autoArray = new PackedAutoArrayRecursion[numElements];
        for (short i = 1; i <= numElements; ++i)
        {
            final PackedAutoArrayRecursion element =
                    new PackedAutoArrayRecursion(i, new PackedAutoArrayRecursion[0]);
            autoArray[i - 1] = element;
        }

        return new PackedAutoArrayRecursion((short)0, autoArray);
    }

    private void checkPackedAutoArrayRecursion(
            PackedAutoArrayRecursion packedAutoArrayRecursion, short numElements)
    {
        assertEquals(0, packedAutoArrayRecursion.getId());
        final PackedAutoArrayRecursion[] autoArray = packedAutoArrayRecursion.getPackedAutoArrayRecursion();
        assertEquals(numElements, autoArray.length);
        for (short i = 1; i <= numElements; ++i)
        {
            final PackedAutoArrayRecursion element = autoArray[i - 1];
            assertEquals(i, element.getId());
            assertEquals(0, element.getPackedAutoArrayRecursion().length);
        }
    }

    private BitBuffer writePackedAutoArrayRecursionToBitBuffer(short numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(0, 8);
            writer.writeVarSize(numElements);
            writer.writeBool(true);
            final byte maxBitNumber = 1;
            writer.writeBits(maxBitNumber, 6);
            writer.writeBits(1, 8);
            writer.writeVarSize(0);
            for (short i = 1; i <= numElements; ++i)
            {
                writer.writeSignedBits(1, maxBitNumber + 1);
                writer.writeVarSize(0);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private int calcPackedAutoArrayRecursionBitSize(short numElements)
    {
        int bitSize = 8; // id
        bitSize += 8; // varsize (length of auto array)
        bitSize += 1; // packing descriptor: is_packed
        if (numElements > 1)
            bitSize += 6; // packing descriptor: max_bit_number
        bitSize += 8 + 8; // first element
        bitSize += (numElements - 1) * (8 + 2); // all deltas

        return bitSize;
    }

    private static final String BLOB_NAME_BASE = "packed_auto_array_struct_recursion_";
    private static final short AUTO_ARRAY_LENGTH1 = 1;
    private static final short AUTO_ARRAY_LENGTH2 = 5;
    private static final short AUTO_ARRAY_LENGTH3 = 10;
}
