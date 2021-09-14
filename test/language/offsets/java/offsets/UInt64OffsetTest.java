package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import offsets.uint64_offset.UInt64Offset;

import org.junit.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UInt64OffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final BitStreamReader reader = prepareReader(false);
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.read(reader);
        assertEquals(OFFSET, uint64Offset.getOffset().longValue());
    }

    @Test(expected=ZserioError.class)
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final BitStreamReader reader = prepareReader(true);
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.read(reader);
    }

    @Test
    public void bitSizeOf() throws ZserioError, IOException
    {
        final UInt64Offset uint64Offset = new UInt64Offset(prepareReader(false));
        assertEquals(BIT_SIZE, uint64Offset.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition() throws ZserioError, IOException
    {
        final UInt64Offset uint64Offset = new UInt64Offset(prepareReader(false));
        assertEquals(BIT_SIZE + 5, uint64Offset.bitSizeOf(3));
    }

    @Test
    public void initializeOffsets()
    {
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.setArray(new byte[ARRAY_SIZE]);
        uint64Offset.initializeOffsets(0);
        assertEquals(OFFSET, uint64Offset.getOffset().longValue());
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.setArray(new byte[ARRAY_SIZE]);
        uint64Offset.initializeOffsets(3);
        // 3 bits start position + 5 bits alignment -> + 1 byte
        assertEquals(OFFSET + 1, uint64Offset.getOffset().longValue());
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.setArray(new byte[ARRAY_SIZE]);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint64Offset.write(writer);
        assertEquals(OFFSET, uint64Offset.getOffset().longValue());
        assertEquals(BitPositionUtil.bitsToBytes(BIT_SIZE), writer.toByteArray().length);
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.setArray(new byte[ARRAY_SIZE]);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(0, 3);
        uint64Offset.write(writer);
        assertEquals(OFFSET + 1, uint64Offset.getOffset().longValue());
        assertEquals(BitPositionUtil.bitsToBytes(BIT_SIZE) + 1, writer.toByteArray().length);
    }

    @Test(expected=ZserioError.class)
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final UInt64Offset uint64Offset = new UInt64Offset();
        uint64Offset.setArray(new byte[ARRAY_SIZE]);
        uint64Offset.setOffset(BigInteger.valueOf(WRONG_OFFSET));
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint64Offset.write(writer, false);
    }

    private BitStreamReader prepareReader(boolean wrongOffset) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        // offset
        writer.writeBigInteger(BigInteger.valueOf(wrongOffset ? WRONG_OFFSET : OFFSET), 64);
        writer.writeVarSize(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; ++i)
        {
            writer.writeByte((byte)0);
        }
        writer.writeInt(0);

        return new ByteArrayBitStreamReader(writer.toByteArray());
    }

    private static final int ARRAY_SIZE = 13;
    private static final int OFFSET = 8 +
            (int)BitPositionUtil.bitsToBytes(BitSizeOfCalculator.getBitSizeOfVarUInt64(ARRAY_SIZE)) +
            ARRAY_SIZE;
    private static final int WRONG_OFFSET = OFFSET + 1;
    private static final int BIT_SIZE = (int)BitPositionUtil.bytesToBits(OFFSET + 4);
}
