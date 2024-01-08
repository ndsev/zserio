package offsets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import offsets.auto_array_offset.AutoArrayHolder;

public class AutoArrayOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffset = false;
        final BitBuffer bitBuffer = writeAutoArrayHolderToBitBuffer(writeWrongOffset);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final AutoArrayHolder autoArrayHolder = new AutoArrayHolder(reader);
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
        assertEquals(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffset = false;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 2;
        assertEquals(AUTO_ARRAY_HOLDER_BIT_SIZE - bitPosition, autoArrayHolder.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 0;
        assertEquals(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition));
        checkAutoArrayHolder(autoArrayHolder);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffset = true;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final int bitPosition = 2;
        assertEquals(AUTO_ARRAY_HOLDER_BIT_SIZE, autoArrayHolder.initializeOffsets(bitPosition));
        checkAutoArrayHolder(autoArrayHolder, bitPosition);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffset = false;
        final AutoArrayHolder autoArrayHolder = createAutoArrayHolder(createWrongOffset);
        final BitBuffer bitBuffer = SerializeUtil.serialize(autoArrayHolder);
        checkAutoArrayHolder(autoArrayHolder);
        final AutoArrayHolder readAutoArrayHolder = SerializeUtil.deserialize(AutoArrayHolder.class, bitBuffer);
        checkAutoArrayHolder(readAutoArrayHolder);
        assertTrue(autoArrayHolder.equals(readAutoArrayHolder));
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

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
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
            writer.writeBits(FORCED_ALIGNMENT_VALUE, 8);
            writer.writeVarSize(AUTO_ARRAY_LENGTH);
            for (int i = 0; i < AUTO_ARRAY_LENGTH; ++i)
                writer.writeBits(i, 7);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkAutoArrayHolder(AutoArrayHolder autoArrayHolder)
    {
        checkAutoArrayHolder(autoArrayHolder, 0);
    }

    private void checkAutoArrayHolder(AutoArrayHolder autoArrayHolder, int bitPosition)
    {
        final long expectedAutoArrayOffset =
                (bitPosition == 0) ? AUTO_ARRAY_OFFSET : AUTO_ARRAY_OFFSET + (bitPosition / 8);
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

    private static final int AUTO_ARRAY_LENGTH = 5;
    private static final byte FORCED_ALIGNMENT_VALUE = 0;

    private static final long WRONG_AUTO_ARRAY_OFFSET = 0;
    private static final long AUTO_ARRAY_OFFSET = 5;

    private static final int AUTO_ARRAY_HOLDER_BIT_SIZE = 32 + 1 + 7 + 8 + AUTO_ARRAY_LENGTH * 7;
}
