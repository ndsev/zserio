package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import offsets.uint64_array_offset.UInt64ArrayOffset;

import org.junit.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.BigIntegerArray;
import zserio.runtime.array.ByteArray;
import zserio.runtime.array.IntArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UInt64ArrayOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final BitStreamReader reader = prepareReader(false);
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.read(reader);
        assertEquals(FIRST_OFFSET, uint64ArrayOffset.getOffsets().elementAt(0).longValue());
    }

    @Test(expected=ZserioError.class)
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final BitStreamReader reader = prepareReader(true);
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.read(reader);
    }

    @Test
    public void bitSizeOf() throws ZserioError, IOException
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset(prepareReader(false));
        assertEquals(BIT_SIZE, uint64ArrayOffset.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition() throws ZserioError, IOException
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset(prepareReader(false));
        assertEquals(BIT_SIZE + 5, uint64ArrayOffset.bitSizeOf(3));
    }

    @Test
    public void initializeOffsets()
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.setOffsets(new BigIntegerArray(VALUES_SIZE));
        uint64ArrayOffset.setArray(new ByteArray(ARRAY_SIZE));
        uint64ArrayOffset.setValues(new IntArray(VALUES_SIZE));
        uint64ArrayOffset.initializeOffsets(0);
        assertEquals(FIRST_OFFSET, uint64ArrayOffset.getOffsets().elementAt(0).longValue());
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.setOffsets(new BigIntegerArray(VALUES_SIZE));
        uint64ArrayOffset.setArray(new ByteArray(ARRAY_SIZE));
        uint64ArrayOffset.setValues(new IntArray(VALUES_SIZE));
        uint64ArrayOffset.initializeOffsets(3);
        // 3 bits start position + 5 bits alignment -> + 1 byte
        assertEquals(FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets().elementAt(0).longValue());
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.setOffsets(new BigIntegerArray(VALUES_SIZE));
        uint64ArrayOffset.setArray(new ByteArray(ARRAY_SIZE));
        uint64ArrayOffset.setValues(new IntArray(VALUES_SIZE));
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint64ArrayOffset.write(writer);
        assertEquals(FIRST_OFFSET, uint64ArrayOffset.getOffsets().elementAt(0).longValue());
        assertEquals(BitPositionUtil.bitsToBytes(BIT_SIZE), writer.toByteArray().length);
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        uint64ArrayOffset.setOffsets(new BigIntegerArray(VALUES_SIZE));
        uint64ArrayOffset.setArray(new ByteArray(ARRAY_SIZE));
        uint64ArrayOffset.setValues(new IntArray(VALUES_SIZE));
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(0, 3);
        uint64ArrayOffset.write(writer);
        assertEquals(FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets().elementAt(0).longValue());
        assertEquals(BitPositionUtil.bitsToBytes(BIT_SIZE) + 1, writer.toByteArray().length);
    }

    @Test(expected=ZserioError.class)
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final UInt64ArrayOffset uint64ArrayOffset = new UInt64ArrayOffset();
        final BigIntegerArray offsets = new BigIntegerArray(VALUES_SIZE);
        for (int i = 0; i < VALUES_SIZE; ++i)
        {
            final long offset = FIRST_OFFSET + i * 4 + (i == VALUES_SIZE - 1 ? 1 : 0);
            offsets.setElementAt(BigInteger.valueOf(offset), i);
        }
        uint64ArrayOffset.setOffsets(offsets);
        uint64ArrayOffset.setArray(new ByteArray(ARRAY_SIZE));
        uint64ArrayOffset.setValues(new IntArray(VALUES_SIZE));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        uint64ArrayOffset.write(writer, false);
    }

    private BitStreamReader prepareReader(boolean wrongOffset) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        // offset
        writer.writeVarSize(VALUES_SIZE);
        for (int i = 0; i < VALUES_SIZE; ++i)
        {
            final long offset = FIRST_OFFSET + i * 4 + (wrongOffset && i == VALUES_SIZE - 1 ? 1 : 0);
            writer.writeBigInteger(BigInteger.valueOf(offset), 64);
        }
        // array
        writer.writeVarSize(ARRAY_SIZE);
        for (int i = 0; i < ARRAY_SIZE; ++i)
        {
            writer.writeByte((byte)0);
        }
        // values
        writer.writeVarSize(VALUES_SIZE);
        for (int i = 0; i < VALUES_SIZE; ++i)
        {
            writer.writeInt(0);
        }

        return new ByteArrayBitStreamReader(writer.toByteArray());
    }

    private static final int ARRAY_SIZE = 13;
    private static final int VALUES_SIZE = 42;
    private static final int FIRST_OFFSET =
            (int)BitPositionUtil.bitsToBytes(BitSizeOfCalculator.getBitSizeOfVarUInt64(VALUES_SIZE)) +
            8 * VALUES_SIZE +
            (int)BitPositionUtil.bitsToBytes(BitSizeOfCalculator.getBitSizeOfVarUInt64(ARRAY_SIZE)) +
            ARRAY_SIZE +
            (int)BitPositionUtil.bitsToBytes(BitSizeOfCalculator.getBitSizeOfVarUInt64(VALUES_SIZE));
    private static final int BIT_SIZE = (int)BitPositionUtil.bytesToBits(FIRST_OFFSET + 4 * VALUES_SIZE);
}
