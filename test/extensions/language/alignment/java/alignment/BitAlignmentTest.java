package alignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import alignment.bit_alignment.BitAlignment;

public class BitAlignmentTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final BitBuffer buffer = writeBitAlignmentToBitBuffer();
        final BitAlignment bitAlignment = SerializeUtil.deserialize(BitAlignment.class, buffer);
        checkBitAlignment(bitAlignment);
    }

    @Test
    public void bitSizeOf()
    {
        final BitAlignment bitAlignment = createBitAlignment();

        assertEquals(BIT_ALIGNMENT_BIT_SIZE, bitAlignment.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final BitAlignment bitAlignment = createBitAlignment();

        // starting at bit position 78, also next 64bits are needed
        int startBitPosition = 0;
        for (; startBitPosition < 78; ++startBitPosition)
            assertEquals(BIT_ALIGNMENT_BIT_SIZE - startBitPosition, bitAlignment.bitSizeOf(startBitPosition));
        // starting at bit position 78, also next 64bits are needed
        assertEquals(BIT_ALIGNMENT_BIT_SIZE - startBitPosition + 64, bitAlignment.bitSizeOf(startBitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final BitAlignment bitAlignment = createBitAlignment();

        // starting up to bit position 77, the structure still fits into original size thanks to alignments
        int bitPosition = 0;
        for (; bitPosition < 78; ++bitPosition)
            assertEquals(BIT_ALIGNMENT_BIT_SIZE, bitAlignment.initializeOffsets(bitPosition));
        // starting at bit position 78, also next 64bits are needed
        assertEquals(BIT_ALIGNMENT_BIT_SIZE + 64, bitAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final BitAlignment bitAlignment = createBitAlignment();
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        bitAlignment.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final BitAlignment readBitAlignment = new BitAlignment(reader);
        checkBitAlignment(readBitAlignment);
        assertTrue(bitAlignment.equals(readBitAlignment));
    }

    private BitBuffer writeBitAlignmentToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(ALIGNED1_FIELD_VALUE, 1);

            writer.writeBits(0, 1);
            writer.writeBits(ALIGNED2_FIELD_VALUE, 2);

            writer.writeBits(0, 2);
            writer.writeBits(ALIGNED3_FIELD_VALUE, 3);

            writer.writeBits(0, 3);
            writer.writeBits(ALIGNED4_FIELD_VALUE, 4);

            writer.writeBits(0, 4);
            writer.writeBits(ALIGNED5_FIELD_VALUE, 5);

            writer.writeBits(0, 5);
            writer.writeBits(ALIGNED6_FIELD_VALUE, 6);

            writer.writeBits(0, 6);
            writer.writeBits(ALIGNED7_FIELD_VALUE, 7);

            writer.writeBits(0, 7);
            writer.writeBits(ALIGNED8_FIELD_VALUE, 8);

            writer.writeBits(0, 1 + 15);
            writer.writeBits(ALIGNED16_FIELD_VALUE, 16);

            writer.writeBits(0, 1 + 31);
            writer.writeBits(ALIGNED32_FIELD_VALUE, 32);

            writer.writeBits(0, 33);
            writer.writeBits(0, 63);
            writer.writeBigInteger(ALIGNED64_FIELD_VALUE, 64);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkBitAlignment(BitAlignment bitAlignment)
    {
        assertEquals(ALIGNED1_FIELD_VALUE, bitAlignment.getAligned1Field());
        assertEquals(ALIGNED2_FIELD_VALUE, bitAlignment.getAligned2Field());
        assertEquals(ALIGNED3_FIELD_VALUE, bitAlignment.getAligned3Field());
        assertEquals(ALIGNED4_FIELD_VALUE, bitAlignment.getAligned4Field());
        assertEquals(ALIGNED5_FIELD_VALUE, bitAlignment.getAligned5Field());
        assertEquals(ALIGNED6_FIELD_VALUE, bitAlignment.getAligned6Field());
        assertEquals(ALIGNED7_FIELD_VALUE, bitAlignment.getAligned7Field());
        assertEquals(ALIGNED8_FIELD_VALUE, bitAlignment.getAligned8Field());
        assertEquals(ALIGNED16_FIELD_VALUE, bitAlignment.getAligned16Field());
        assertEquals(ALIGNED32_FIELD_VALUE, bitAlignment.getAligned32Field());
        assertEquals(ALIGNED64_FIELD_VALUE, bitAlignment.getAligned64Field());
    }

    private static BitAlignment createBitAlignment()
    {
        return new BitAlignment(ALIGNED1_FIELD_VALUE, ALIGNED2_FIELD_VALUE, ALIGNED3_FIELD_VALUE,
                ALIGNED4_FIELD_VALUE, ALIGNED5_FIELD_VALUE, ALIGNED6_FIELD_VALUE, ALIGNED7_FIELD_VALUE,
                ALIGNED8_FIELD_VALUE, (byte)0, ALIGNED16_FIELD_VALUE, (byte)0, ALIGNED32_FIELD_VALUE, 0,
                ALIGNED64_FIELD_VALUE);
    }

    private static final int BIT_ALIGNMENT_BIT_SIZE = 320;

    private static final byte ALIGNED1_FIELD_VALUE = (byte)1;
    private static final byte ALIGNED2_FIELD_VALUE = (byte)2;
    private static final byte ALIGNED3_FIELD_VALUE = (byte)5;
    private static final byte ALIGNED4_FIELD_VALUE = (byte)13;
    private static final byte ALIGNED5_FIELD_VALUE = (byte)26;
    private static final byte ALIGNED6_FIELD_VALUE = (byte)56;
    private static final byte ALIGNED7_FIELD_VALUE = (byte)88;
    private static final short ALIGNED8_FIELD_VALUE = (short)222;
    private static final int ALIGNED16_FIELD_VALUE = 0xcafe;
    private static final long ALIGNED32_FIELD_VALUE = 0xcafec0deL;
    private static final java.math.BigInteger ALIGNED64_FIELD_VALUE =
            new java.math.BigInteger("cafec0dedeadface", 16);
}
