package offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import offsets.packed_auto_array_offset.AutoArrayHolder;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class PackedAutoArrayOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffset = false;
        final BitBuffer bitBuffer = writeAutoArrayHolderToBitBuffer(writeWrongOffset);
        final BitStreamReader reader= new ByteArrayBitStreamReader(bitBuffer);
        final AutoArrayHolder autoArrayHolder = new AutoArrayHolder();
        autoArrayHolder.read(reader);
        checkAutoArrayHolder(autoArrayHolder);
    }

    @Test
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffset = true;
        final BitBuffer bitBuffer = writeAutoArrayHolderToBitBuffer(writeWrongOffset);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new AutoArrayHolder(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffset = false;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int autoArrayHolderBitSize = calcAutoArrayHolderBitSize();
        assertEquals(autoArrayHolderBitSize, autoArrayHolder.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffset = false;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 2;
        final int autoArrayHolderBitSize = calcAutoArrayHolderBitSize();
        assertEquals(autoArrayHolderBitSize - bitPosition, autoArrayHolder.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 0;
        final int expectedEndBitPosition = calcAutoArrayHolderBitSize();
        assertEquals(expectedEndBitPosition, autoArrayHolder.initializeOffsets(bitPosition));
        checkAutoArrayHolder(autoArrayHolder);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 2;
        final int expectedEndBitPosition = calcAutoArrayHolderBitSize();
        assertEquals(expectedEndBitPosition, autoArrayHolder.initializeOffsets(bitPosition));
        checkAutoArrayHolder(autoArrayHolder, bitPosition);
    }

    @Test
    public void writeReadFile() throws IOException, ZserioError
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        SerializeUtil.serializeToFile(autoArrayHolder, BLOB_NAME);
        checkAutoArrayHolder(autoArrayHolder);
        final AutoArrayHolder readAutoArrayHolder = SerializeUtil.deserializeFromFile(AutoArrayHolder.class,
                BLOB_NAME);
        checkAutoArrayHolder(readAutoArrayHolder);
        assertEquals(autoArrayHolder, readAutoArrayHolder);
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 2;
        writer.writeBits(0, bitPosition);
        autoArrayHolder.initializeOffsets(writer.getBitPosition());
        autoArrayHolder.write(writer);
        checkAutoArrayHolder(autoArrayHolder, bitPosition);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final AutoArrayHolder readAutoArrayHolder = new AutoArrayHolder(reader);
        checkAutoArrayHolder(readAutoArrayHolder, bitPosition);
        assertTrue(autoArrayHolder.equals(readAutoArrayHolder));
    }

    @Test
    public void writeWrongOffset() throws ZserioError, IOException
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> autoArrayHolder.write(writer));
        writer.close();
    }

    private BitBuffer writeAutoArrayHolderToBitBuffer(boolean writeWrongOffset) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedInt((writeWrongOffset) ? WRONG_AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET);
            writer.writeBits(FORCED_ALIGNMENT_VALUE, 1);
            writer.alignTo(8);

            writer.writeVarSize(AUTO_ARRAY_LENGTH);
            writer.writeBool(true);
            writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
            writer.writeBits(0, 7);
            for (int i = 0; i < AUTO_ARRAY_LENGTH - 1; ++i)
                writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkAutoArrayHolder(AutoArrayHolder autoArrayHolder)
    {
        checkAutoArrayHolder(autoArrayHolder, 0);
    }

    private void checkAutoArrayHolder(AutoArrayHolder autoArrayHolder, int bitPosition)
    {
        final long expectedAutoArrayOffset = (bitPosition == 0) ? AUTO_ARRAY_OFFSET :
            AUTO_ARRAY_OFFSET + (bitPosition / 8);
        assertEquals(expectedAutoArrayOffset, autoArrayHolder.getAutoArrayOffset());

        assertEquals(FORCED_ALIGNMENT_VALUE, autoArrayHolder.getForceAlignment());

        final byte[] autoArray = autoArrayHolder.getAutoArray();
        assertEquals(AUTO_ARRAY_LENGTH, autoArray.length);
        for (int i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            assertEquals((byte)i, autoArray[i]);
    }

    private AutoArrayHolder createAutoArrayHolder(boolean createWrongOffset)
    {
        final long autoArrayOffset = (createWrongOffset) ? WRONG_AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET;
        final byte[] autoArray = new byte[AUTO_ARRAY_LENGTH];
        for (int i = 0; i < AUTO_ARRAY_LENGTH; ++i)
            autoArray[i] = (byte)i;

        return new AutoArrayHolder(autoArrayOffset, FORCED_ALIGNMENT_VALUE, autoArray);
    }

    private int calcAutoArrayHolderBitSize()
    {
        int bitSize = 32; // field: autoArrayOffset
        bitSize += 1; // field: forceAlignment
        bitSize += 7; // padding because of alignment
        bitSize += 8; // auto varsize
        bitSize += 1; // packing descriptor: is_packed
        bitSize += 6; // packing descriptor: max_bit_number
        bitSize += 7; // first element
        bitSize += (AUTO_ARRAY_LENGTH - 1) * (PACKED_ARRAY_MAX_BIT_NUMBER + 1); // all deltas

        return bitSize;
    }

    private static final String BLOB_NAME = "packed_auto_array_offset.blob";

    private static final int    AUTO_ARRAY_LENGTH = 5;
    private static final byte   FORCED_ALIGNMENT_VALUE = 0;

    private static final long   WRONG_AUTO_ARRAY_OFFSET = 0;
    private static final long   AUTO_ARRAY_OFFSET = 5;

    private static final short  PACKED_ARRAY_DELTA = 1;
    private static final short  PACKED_ARRAY_MAX_BIT_NUMBER = 1;
}
