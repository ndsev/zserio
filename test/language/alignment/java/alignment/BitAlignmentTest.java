package alignment;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import alignment.bit_alignment.BitAlignment;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class BitAlignmentTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeBitAlignmentToFile(file);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final BitAlignment bitAlignment = new BitAlignment(stream);
        stream.close();
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
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        bitAlignment.write(writer);
        writer.close();
        final BitAlignment readBitAlignment = new BitAlignment(file);
        checkBitAlignment(readBitAlignment);
        assertTrue(bitAlignment.equals(readBitAlignment));
    }

    private void writeBitAlignmentToFile(File file) throws IOException
    {
        final FileBitStreamWriter stream = new FileBitStreamWriter(file);

        stream.writeBits(ALIGNED1_FIELD_VALUE, 1);

        stream.writeBits(0, 1);
        stream.writeBits(ALIGNED2_FIELD_VALUE, 2);

        stream.writeBits(0, 2);
        stream.writeBits(ALIGNED3_FIELD_VALUE, 3);

        stream.writeBits(0, 3);
        stream.writeBits(ALIGNED4_FIELD_VALUE, 4);

        stream.writeBits(0, 4);
        stream.writeBits(ALIGNED5_FIELD_VALUE, 5);

        stream.writeBits(0, 5);
        stream.writeBits(ALIGNED6_FIELD_VALUE, 6);

        stream.writeBits(0, 6);
        stream.writeBits(ALIGNED7_FIELD_VALUE, 7);

        stream.writeBits(0, 7);
        stream.writeBits(ALIGNED8_FIELD_VALUE, 8);

        stream.writeBits(0, 1 + 15);
        stream.writeBits(ALIGNED16_FIELD_VALUE, 16);

        stream.writeBits(0, 1 + 31);
        stream.writeBits(ALIGNED32_FIELD_VALUE, 32);

        stream.writeBits(0, 33);
        stream.writeBits(0, 63);
        stream.writeBigInteger(ALIGNED64_FIELD_VALUE, 64);

        stream.close();
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
        return new BitAlignment(ALIGNED1_FIELD_VALUE, ALIGNED2_FIELD_VALUE,
                ALIGNED3_FIELD_VALUE, ALIGNED4_FIELD_VALUE, ALIGNED5_FIELD_VALUE, ALIGNED6_FIELD_VALUE,
                ALIGNED7_FIELD_VALUE, ALIGNED8_FIELD_VALUE, (byte) 0, ALIGNED16_FIELD_VALUE,
                (byte) 0, ALIGNED32_FIELD_VALUE, 0, ALIGNED64_FIELD_VALUE);
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
