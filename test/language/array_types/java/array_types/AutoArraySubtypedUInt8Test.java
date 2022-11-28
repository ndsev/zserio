package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;
import array_types.auto_array_subtyped_uint8.AutoArray;

public class AutoArraySubtypedUInt8Test
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
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH2);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final short[] array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final AutoArray autoArray = new AutoArray(array);
        final int bitPosition = 2;
        final int autoArrayBitSize = 8 + numElements * 8;
        assertEquals(autoArrayBitSize, autoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final short[] array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final AutoArray autoArray = new AutoArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        assertEquals(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final BitBuffer buffer = writeAutoArrayToBitBuffer(numElements);
        final AutoArray autoArray = SerializeUtil.deserialize(AutoArray.class, buffer);

        final short[] array = autoArray.getArray();
        assertEquals(numElements, array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, array[i]);
    }

    private void checkWriteRead(short numElements) throws IOException, ZserioError
    {
        final short[] array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final AutoArray autoArray = new AutoArray(array);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        SerializeUtil.serializeToFile(autoArray, file);

        final AutoArray readAutoArray = SerializeUtil.deserializeFromFile(AutoArray.class, file);
        final short[] readArray = readAutoArray.getArray();
        assertEquals(numElements, readArray.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readArray[i]);
    }

    private BitBuffer writeAutoArrayToBitBuffer(short numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeVarSize(numElements);
            for (short i = 0; i < numElements; ++i)
                writer.writeUnsignedByte(i);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static String BLOB_NAME_BASE = "auto_array_subtyped_uint8_";
    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
