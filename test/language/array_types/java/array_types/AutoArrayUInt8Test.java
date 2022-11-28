package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;
import array_types.auto_array_uint8.AutoArray;

public class AutoArrayUInt8Test
{
    @Test
    public void emptyConstructor()
    {
        final AutoArray autoArray = new AutoArray();
        assertEquals(null, autoArray.getUint8Array());
    }

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
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
        final int bitPosition = 2;
        final int autoArrayBitSize = 8 + numElements * 8;
        assertEquals(autoArrayBitSize, autoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        assertEquals(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final BitBuffer buffer = writeAutoArrayToBitBuffer(numElements);
        final AutoArray autoArray = SerializeUtil.deserialize(AutoArray.class, buffer);

        final short[] uint8Array = autoArray.getUint8Array();
        assertEquals(numElements, uint8Array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, uint8Array[i]);
    }

    private void checkWriteRead(short numElements) throws IOException, ZserioError
    {
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        SerializeUtil.serializeToFile(autoArray, file);

        final AutoArray readAutoArray = SerializeUtil.deserializeFromFile(AutoArray.class, file);
        final short[] readUint8Array = readAutoArray.getUint8Array();
        assertEquals(numElements, readUint8Array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readUint8Array[i]);
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

    private static final String BLOB_NAME_BASE = "auto_array_uint8_";
    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
