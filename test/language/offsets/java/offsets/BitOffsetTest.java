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

import offsets.bit_offset.BitOffset;

public class BitOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeBitOffsetToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final BitOffset bitOffset = new BitOffset();
        bitOffset.read(reader);
        checkBitOffset(bitOffset);
    }

    @Test
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final BitBuffer bitBuffer = writeBitOffsetToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new BitOffset(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        assertEquals(BIT_OFFSET_BIT_SIZE, bitOffset.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final int bitPosition = 1;
        assertEquals(BIT_OFFSET_BIT_SIZE + 7, bitOffset.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(BIT_OFFSET_BIT_SIZE, bitOffset.initializeOffsets(bitPosition));
        checkBitOffset(bitOffset);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final int bitPosition = 2;
        assertEquals(BIT_OFFSET_BIT_SIZE + bitPosition + 6, bitOffset.initializeOffsets(bitPosition));

        final short offsetShift = 1;
        checkOffsets(bitOffset, offsetShift);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(bitOffset);
        checkBitOffset(bitOffset);

        final BitOffset readBitOffset = SerializeUtil.deserialize(BitOffset.class, bitBuffer);
        checkBitOffset(readBitOffset);
        assertTrue(bitOffset.equals(readBitOffset));
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 2;
        writer.writeBits(0, bitPosition);
        bitOffset.initializeOffsets(writer.getBitPosition());
        bitOffset.write(writer);
        final short offsetShift = 1;
        checkOffsets(bitOffset, offsetShift);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final BitOffset readBitOffset = new BitOffset(reader);
        checkOffsets(readBitOffset, offsetShift);
        assertTrue(bitOffset.equals(readBitOffset));
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final BitOffset bitOffset = createBitOffset(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> bitOffset.write(writer));
        writer.close();
    }

    private BitBuffer writeBitOffsetToBitBuffer(boolean writeWrongOffsets) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (writeWrongOffsets)
            {
                writer.writeUnsignedByte(WRONG_FIELD1_OFFSET);
                writer.writeUnsignedShort(WRONG_FIELD2_OFFSET);
                writer.writeUnsignedInt(WRONG_FIELD3_OFFSET);
                writer.writeBits(WRONG_FIELD4_OFFSET, 8);
                writer.writeBits(WRONG_FIELD5_OFFSET, 15);
                writer.writeBits(WRONG_FIELD6_OFFSET, 18);
                writer.writeBits(WRONG_FIELD7_OFFSET, 23);
                writer.writeBits(WRONG_FIELD8_OFFSET, 8);
            }
            else
            {
                writer.writeUnsignedByte(FIELD1_OFFSET);
                writer.writeUnsignedShort(FIELD2_OFFSET);
                writer.writeUnsignedInt(FIELD3_OFFSET);
                writer.writeBits(FIELD4_OFFSET, 8);
                writer.writeBits(FIELD5_OFFSET, 15);
                writer.writeBits(FIELD6_OFFSET, 18);
                writer.writeBits(FIELD7_OFFSET, 23);
                writer.writeBits(FIELD8_OFFSET, 8);
            }

            writer.writeBits(FIELD1_VALUE, 1);

            writer.writeBits(0, 7);
            writer.writeBits(FIELD2_VALUE, 2);

            writer.writeBits(0, 6);
            writer.writeBits(FIELD3_VALUE, 3);

            writer.writeBits(0, 5);
            writer.writeBits(FIELD4_VALUE, 4);

            writer.writeBits(0, 4);
            writer.writeBits(FIELD5_VALUE, 5);

            writer.writeBits(0, 3);
            writer.writeBits(FIELD6_VALUE, 6);

            writer.writeBits(0, 2);
            writer.writeBits(FIELD7_VALUE, 7);

            writer.writeBits(0, 1);
            writer.writeBits(FIELD8_VALUE, 8);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkOffsets(BitOffset bitOffset, short offsetShift)
    {
        assertEquals(FIELD1_OFFSET + offsetShift, bitOffset.getField1Offset());
        assertEquals(FIELD2_OFFSET + offsetShift, bitOffset.getField2Offset());
        assertEquals(FIELD3_OFFSET + offsetShift, bitOffset.getField3Offset());
        assertEquals(FIELD4_OFFSET + offsetShift, bitOffset.getField4Offset());
        assertEquals(FIELD5_OFFSET + offsetShift, bitOffset.getField5Offset());
        assertEquals(FIELD6_OFFSET + offsetShift, bitOffset.getField6Offset());
        assertEquals(FIELD7_OFFSET + offsetShift, bitOffset.getField7Offset());
        assertEquals(FIELD8_OFFSET + offsetShift, bitOffset.getField8Offset());
    }

    private void checkBitOffset(BitOffset bitOffset)
    {
        final short offsetShift = 0;
        checkOffsets(bitOffset, offsetShift);

        assertEquals(FIELD1_VALUE, bitOffset.getField1());
        assertEquals(FIELD2_VALUE, bitOffset.getField2());
        assertEquals(FIELD3_VALUE, bitOffset.getField3());
        assertEquals(FIELD4_VALUE, bitOffset.getField4());
        assertEquals(FIELD5_VALUE, bitOffset.getField5());
        assertEquals(FIELD6_VALUE, bitOffset.getField6());
        assertEquals(FIELD7_VALUE, bitOffset.getField7());
        assertEquals(FIELD8_VALUE, bitOffset.getField8());
    }

    private BitOffset createBitOffset(boolean createWrongOffsets)
    {
        final BitOffset bitOffset = new BitOffset();

        if (createWrongOffsets)
        {
            bitOffset.setField1Offset(WRONG_FIELD1_OFFSET);
            bitOffset.setField2Offset(WRONG_FIELD2_OFFSET);
            bitOffset.setField3Offset(WRONG_FIELD3_OFFSET);
            bitOffset.setField4Offset(WRONG_FIELD4_OFFSET);
            bitOffset.setField5Offset(WRONG_FIELD5_OFFSET);
            bitOffset.setField6Offset(WRONG_FIELD6_OFFSET);
            bitOffset.setField7Offset(WRONG_FIELD7_OFFSET);
            bitOffset.setField8Offset(WRONG_FIELD8_OFFSET);
        }
        else
        {
            bitOffset.setField1Offset(FIELD1_OFFSET);
            bitOffset.setField2Offset(FIELD2_OFFSET);
            bitOffset.setField3Offset(FIELD3_OFFSET);
            bitOffset.setField4Offset(FIELD4_OFFSET);
            bitOffset.setField5Offset(FIELD5_OFFSET);
            bitOffset.setField6Offset(FIELD6_OFFSET);
            bitOffset.setField7Offset(FIELD7_OFFSET);
            bitOffset.setField8Offset(FIELD8_OFFSET);
        }

        bitOffset.setField1(FIELD1_VALUE);
        bitOffset.setField2(FIELD2_VALUE);
        bitOffset.setField3(FIELD3_VALUE);
        bitOffset.setField4(FIELD4_VALUE);
        bitOffset.setField5(FIELD5_VALUE);
        bitOffset.setField6(FIELD6_VALUE);
        bitOffset.setField7(FIELD7_VALUE);
        bitOffset.setField8(FIELD8_VALUE);

        return bitOffset;
    }

    private static final int BIT_OFFSET_BIT_SIZE = 192;

    private static final short WRONG_FIELD1_OFFSET = (short)0;
    private static final int WRONG_FIELD2_OFFSET = (int)0;
    private static final long WRONG_FIELD3_OFFSET = (long)0;
    private static final short WRONG_FIELD4_OFFSET = (short)0;
    private static final short WRONG_FIELD5_OFFSET = (short)0;
    private static final int WRONG_FIELD6_OFFSET = (int)0;
    private static final int WRONG_FIELD7_OFFSET = (int)0;
    private static final short WRONG_FIELD8_OFFSET = (short)0;

    private static final short FIELD1_OFFSET = (short)16;
    private static final int FIELD2_OFFSET = (int)17;
    private static final long FIELD3_OFFSET = (long)18;
    private static final short FIELD4_OFFSET = (short)19;
    private static final short FIELD5_OFFSET = (short)20;
    private static final int FIELD6_OFFSET = (int)21;
    private static final int FIELD7_OFFSET = (int)22;
    private static final short FIELD8_OFFSET = (short)23;

    private static final byte FIELD1_VALUE = (byte)1;
    private static final byte FIELD2_VALUE = (byte)2;
    private static final byte FIELD3_VALUE = (byte)5;
    private static final byte FIELD4_VALUE = (byte)13;
    private static final byte FIELD5_VALUE = (byte)26;
    private static final byte FIELD6_VALUE = (byte)56;
    private static final byte FIELD7_VALUE = (byte)88;
    private static final short FIELD8_VALUE = (short)222;
}
