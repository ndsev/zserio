package zserio.runtime.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.FloatUtil;

/**
 * A bit stream reader using byte array.
 */
public class ByteArrayBitStreamReader extends ByteArrayBitStreamBase implements BitStreamReader
{
    /**
     * Constructs object containing given bytes with a given byte order.
     *
     * @param bytes     Array of bytes to construct from.
     */
    public ByteArrayBitStreamReader(final byte[] bytes)
    {
        this.buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
    }

    @Override
    public long readSignedBits(final int numBits) throws IOException
    {
        long result = readBits(numBits);

        /*
         * Perform a sign extension if needed.
         * 1L << 64 in Java is not 0L, but 1L, so treat numBits == 64 as a special case
         * (numBits == 64 does not need sign extension anyway)
         */
        if (numBits < 64 && (result & (1L << (numBits - 1))) != 0)
        {
            result |= (-1L << numBits);
        }
        return result;
    }

    @Override
    public long readBits(final int numBits) throws IOException
    {
        checkRange(numBits);

        int bitsToRead = bitOffset + numBits;
        final int nextBitOffset = bitsToRead & BYTE_MOD_MASK;

        long accum = nextUnsignedByte() & BIT_MASKS[bitOffset];
        bitsToRead -= BITS_PER_BYTE;

        if (bitsToRead < 0)
        {
            // less than a byte needed
            accum = accum >>> -bitsToRead;
            bytePosition--; // consumed only few bits
        }
        else
        {
            // full bytes
            while (bitsToRead >= BITS_PER_BYTE)
            {
                accum = (accum << BITS_PER_BYTE) | nextUnsignedByte();
                bitsToRead -= BITS_PER_BYTE;
            }

            // last few bits
            if (bitsToRead > 0)
            {
                accum = (accum << bitsToRead) | (nextUnsignedByte() >>> (BITS_PER_BYTE - bitsToRead));
                bytePosition--; // consumed only few bits
            }
        }

        bitOffset = nextBitOffset;

        return accum;
    }

    @Override
    public byte readByte() throws IOException
    {
        byte result;
        if (bitOffset == 0)
        {
            result = nextByte();
        }
        else
        {
            result = (byte)readBits(BITS_PER_BYTE);
        }
        return result;
    }

    @Override
    public short readUnsignedByte() throws IOException
    {
        return (short)(readByte() & 0xff);
    }

    @Override
    public short readShort() throws IOException
    {
        short result;
        if (bitOffset == 0)
        {
            final byte b0 = nextByte();
            final byte b1 = nextByte();
            result = makeShort(b0, b1);
        }
        else
        {
            result = (short) readBits(BITS_PER_SHORT);
        }
        return result;
    }

    @Override
    public int readUnsignedShort() throws IOException
    {
        return readShort() & 0xffff;
    }

    @Override
    public int readInt() throws IOException
    {
        int result;
        if (bitOffset == 0)
        {
            final byte b0 = nextByte();
            final byte b1 = nextByte();
            final byte b2 = nextByte();
            final byte b3 = nextByte();
            result = makeInt(b0, b1, b2, b3);
        }
        else
        {
            result = (int)readBits(BITS_PER_INT);
        }
        return result;
    }

    @Override
    public long readUnsignedInt() throws IOException
    {
        return readInt() & 0xffffffffL;
    }

    @Override
    public long readLong() throws IOException
    {
        long result;
        if (bitOffset == 0)
        {
            final byte b0 = nextByte();
            final byte b1 = nextByte();
            final byte b2 = nextByte();
            final byte b3 = nextByte();
            final byte b4 = nextByte();
            final byte b5 = nextByte();
            final byte b6 = nextByte();
            final byte b7 = nextByte();
            result = makeLong(b0, b1, b2, b3, b4, b5, b6, b7);
        }
        else
        {
            result = readBits(BITS_PER_LONG);
        }
        return result;
    }

    @Override
    public BigInteger readBigInteger(final int numBits) throws IOException
    {
        BigInteger result = BigInteger.ZERO;
        int bitsToRead = numBits;
        if (bitsToRead > BITS_PER_BYTE)
        {
            if (bitOffset != 0)
            {
                final int prefixLength = BITS_PER_BYTE - bitOffset;
                final long mostSignificantBits = readBits(prefixLength);
                result = BigInteger.valueOf(mostSignificantBits);
                bitsToRead -= prefixLength;
            }

            final int numBytes = bitsToRead / BITS_PER_BYTE;
            final byte[] b = new byte[numBytes];
            readFully(b);
            final BigInteger i = new BigInteger(1, b);
            result = result.shiftLeft(BITS_PER_BYTE * numBytes);
            result = result.or(i);
            bitsToRead &= BYTE_MOD_MASK;
        }
        if (bitsToRead > 0)
        {
            final long value = readBits(bitsToRead);
            result = result.shiftLeft(bitsToRead);
            result = result.or(BigInteger.valueOf(value));
        }
        return result;
    }

    @Override
    public BigInteger readSignedBigInteger(final int numBits) throws IOException
    {
        BigInteger result = readBigInteger(numBits);
        if (result.testBit(numBits - 1))
        {
            result = result.subtract(BigInteger.ONE.shiftLeft(numBits));
        }
        return result;
    }

    @Override
    public float readFloat16() throws IOException
    {
        final short halfPrecisionFloatValue = readShort();

        return FloatUtil.convertShortToFloat(halfPrecisionFloatValue);
    }

    @Override
    public float readFloat32() throws IOException
    {
        final int singlePrecisionFloatValue = readInt();

        return FloatUtil.convertIntToFloat(singlePrecisionFloatValue);
    }

    @Override
    public double readFloat64() throws IOException
    {
        final long doublePrecisionFloatValue = readLong();

        return FloatUtil.convertLongToDouble(doublePrecisionFloatValue);
    }

    @Override
    public String readString() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final long numBytes = readVarUInt64();
        for (long i = 0; i < numBytes; i++)
        {
            baos.write(readByte());
        }
        baos.close();
        return new String(baos.toByteArray(), DEFAULT_CHARSET_NAME);
    }

    @Override
    public boolean readBool() throws IOException
    {
        return readBits(1) == 1;
    }

    @Override
    public short readVarInt16() throws IOException
    {
        return (short)readVarNum(true, 2);
    }

    @Override
    public int readVarInt32() throws IOException
    {
        return (int)readVarNum(true, 4);
    }

    @Override
    public long readVarInt64() throws IOException
    {
        return readVarNum(true, 8);
    }

    @Override
    public short readVarUInt16() throws IOException
    {
        return (short)readVarNum(false, 2);
    }

    @Override
    public int readVarUInt32() throws IOException
    {
        return (int)readVarNum(false, 4);
    }

    @Override
    public long readVarUInt64() throws IOException
    {
        return readVarNum(false, 8);
    }

    @Override
    public long readVarInt() throws IOException
    {
        long b = readBits(8); // byte 1
        final boolean sign = (b & VARINT_SIGN_1) != 0;
        long result = b & VARINT_BYTE_1;
        if ((b & VARINT_HAS_NEXT_1) == 0)
            return sign == true ? (result == 0 ? Long.MIN_VALUE : -result) : result;

        b = readBits(8); // byte 2
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 3
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 4
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 5
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 6
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 7
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = readBits(8); // byte 8
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        // byte 9
        result = result << 8 | readBits(8);
        return sign == true ? -result : result;
    }

    @Override
    public BigInteger readVarUInt() throws IOException
    {
        long b = readBits(8); // byte 1
        BigInteger result = BigInteger.valueOf(b & VARUINT_BYTE);
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 2
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 3
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 4
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 5
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 6
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 7
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 8
        result = result.shiftLeft(7).or(BigInteger.valueOf(b & VARUINT_BYTE));
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        // byte 9
        result = result.shiftLeft(8).or(BigInteger.valueOf(readBits(8) & 0xFF));
        return result;
    }

    @Override
    public void alignTo(final int alignVal) throws IOException
    {
        final long offset = getBitPosition() % alignVal;
        if (offset != 0)
        {
            final int skip = (int)(alignVal - offset);
            skipBits(skip);
        }
    }

    @Override
    public void close() throws IOException
    {
        // nothing to do
    }

    protected byte[] getBuffer()
    {
        return buffer;
    }

    private void readFully(final byte[] b) throws IOException
    {
        readFully(b, 0, b.length);
    }

    private void readFully(final byte[] dest, final int offset, final int length) throws IOException
    {
        int count = read(dest, offset, length);
        if (count != length)
            throw new EOFException("ByteArrayBitStreamReader: Wrong number of read bytes. Read was " + count +
                    " but requested was " + length + ".");
    }

    private int read(final byte[] dest, final int offset, final int length)
    {
        System.arraycopy(buffer, bytePosition, dest, offset, length);
        bytePosition += length;
        return length;
    }

    private void skipBits(final int bitCnt) throws IOException
    {
        setBitPosition(getBitPosition() + bitCnt);
    }

    /**
     * Returns the next byte without resetting the bit offset.
     *
     * @return Read next byte.
     *
     * @throws IOException If the reading failed.
     */
    private byte nextByte() throws IOException
    {
        if (bytePosition >= buffer.length)
            throw new IOException("ByteArrayBitStreamReader: Unable to read byte on offset position " +
                    (bytePosition + 1) + ". It's beyond end of the stream with length " + buffer.length + ".");

        return buffer[bytePosition++];
    }

    /**
     * Returns the next unsigned byte without resetting the bit offset.
     *
     * @return Read next byte.
     *
     * @throws IOException If the reading failed.
     */
    private int nextUnsignedByte() throws IOException
    {
        return nextByte() & 0xff;
    }

    /**
     * Creates a short value as concatenation of the given two bytes.
     *
     * @param b1 Byte which represents bit 8 to 15.
     * @param b0 Byte which represents bit 0 to 7.
     *
     * @return Concatenated short value.
     */
    private static short makeShort(final byte b1, final byte b0)
    {
        return (short)((b1 << 8) | (b0 & 0xff));
    }

    /**
     * Creates an integer as concatenation of the given four byte values.
     *
     * @param b3 Byte which represents bit 24 to 31.
     * @param b2 Byte which represents bit 16 to 23.
     * @param b1 Byte which represents bit 8 to 15.
     * @param b0 Byte which represents bit 0 to 7.
     *
     * @return The concatenated integer value.
     */
    private static int makeInt(final byte b3, final byte b2, final byte b1, final byte b0)
    {
        return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
    }

    /**
     * Creates a long as concatenation of the given eight byte values.
     *
     * @param b7 Byte which represents bit 56 to 63.
     * @param b6 Byte which represents bit 48 to 55.
     * @param b5 Byte which represents bit 40 to 47.
     * @param b4 Byte which represents bit 32 to 39.
     * @param b3 Byte which represents bit 24 to 31.
     * @param b2 Byte which represents bit 16 to 23.
     * @param b1 Byte which represents bit 8 to 15.
     * @param b0 Byte which represents bit 0 to 7.
     *
     * @return The concatenation of the eight byte values as long value.
     */
    private static long makeLong(final byte b7, final byte b6, final byte b5, final byte b4, final byte b3,
            final byte b2, final byte b1, final byte b0)
    {
        return ((((long)b7) << 56) | (((long)b6 & 0xff) << 48) | (((long)b5 & 0xff) << 40) |
                (((long)b4 & 0xff) << 32) | (((long)b3 & 0xff) << 24) | (((long)b2 & 0xff) << 16) |
                (((long)b1 & 0xff) << 8) | (((long)b0 & 0xff)));
    }

    /**
     * Reads the next variable number depending on the given signed flag and the maximum variable byte size.
     *
     * @param signed      A flag indicating if the number is signed or unsigned.
     * @param maxVarBytes The maximum variable byte size.
     *
     * @return Read variable number as long value.
     *
     * @throws IOException If the byte reading failed.
     */
    private long readVarNum(final boolean signed, final int maxVarBytes) throws IOException
    {
        long result = 0;
        boolean sign = false;
        boolean hasNextByte = true;

        for (int i = 0; i < maxVarBytes && hasNextByte; i++)
        {
            final byte b = readByte();
            int numBits = BITS_PER_BYTE;
            if (signed && i == 0)
            {
                sign = ((b >>> --numBits) & 1) == 1;
            }
            if (i < maxVarBytes - 1)
            {
                hasNextByte = ((b >>> --numBits) & 1) == 1;
            }
            else
            {
                hasNextByte = false;
            }
            result = (result << numBits) | (b & (-1L >>> (BITS_PER_LONG - numBits)));
        }
        return sign ? -result : result;
    }

    /**
     * Bit masks to mask appropriate bits during unaligned reading.
     */
    private static final int BIT_MASKS[] = { 0xff, 0x7f, 0x3f, 0x1f, 0x0f, 0x07, 0x03, 0x01 };

    /** Variable length integer sing bit mask for first byte. */
    private static final int VARINT_SIGN_1 = 0x80;
    /** Variable length integer value bit mask for first byte. */
    private static final int VARINT_BYTE_1 = 0x3f;
    /** Variable length integer value bit mask for intermediate bytes. */
    private static final int VARINT_BYTE_N = 0x7f;
    /** Variable length integer 'has next' bit mask for first byte. */
    private static final int VARINT_HAS_NEXT_1 = 0x40;
    /** Variable length integer 'has next' bit mask for intermediate bytes. */
    private static final int VARINT_HAS_NEXT_N = 0x80;

    /** Variable length integer value bit mask. */
    private static final int VARUINT_BYTE = 0x7f;
    /** Variable length integer 'has next' bit mask. */
    private static final int VARUINT_HAS_NEXT = 0x80;

    /**
     * The underlying byte array.
     */
    private final byte[] buffer;
}
