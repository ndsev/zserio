package zserio.runtime.io;

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
     * @param bytes Array of bytes to construct from.
     */
    public ByteArrayBitStreamReader(final byte[] bytes)
    {
        this.buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
        this.lastByteBits = 8;
    }

    /**
     * Constructs object using given bit buffer.
     *
     * @param bitBuffer Bit buffer to construct from.
     */
    public ByteArrayBitStreamReader(final BitBuffer bitBuffer)
    {
        buffer = bitBuffer.getBuffer();
        final byte lastBits = (byte)(bitBuffer.getBitSize() % 8);
        lastByteBits = lastBits == 0 ? 8 : lastBits;
    }

    /**
     * Constructs object containing given bytes with a given byte order with exact bit size.
     *
     * @param bytes Array of bytes to construct from.
     * @param bitSize Size of the buffer in bits.
     */
    public ByteArrayBitStreamReader(final byte[] bytes, long bitSize)
    {
        this.buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
        final byte lastBits = (byte)(bitSize % 8);
        lastByteBits = lastBits == 0 ? 8 : lastBits;
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
        long accum = nextUnsignedByte() & BIT_MASKS[bitOffset];
        bitsToRead -= 8;

        if (bitsToRead < 0)
        {
            // less than already read byte is needed
            accum = accum >>> -bitsToRead;
            bytePosition--; // consumed only few bits
        }
        else
        {
            // full bytes
            while (bitsToRead >= 8)
            {
                accum = (accum << 8) | nextUnsignedByte();
                bitsToRead -= 8;
            }

            // last few bits
            if (bitsToRead > 0)
            {
                accum = (accum << bitsToRead) | (nextUnsignedByte() >>> (8 - bitsToRead));
                bytePosition--; // consumed only few bits
            }
        }

        bitOffset = (bitOffset + numBits) & BYTE_MOD_MASK;

        if (bytePosition == buffer.length) // consumed full last byte
        {
            if (lastByteBits < 8) // check if whole byte is available
            {
                throw new IOException("ByteArrayBitStreamReader: Unable to read bit on offset position " +
                        lastByteBits + " in the last byte.");
            }
        }
        else if (bytePosition + 1 >= buffer.length) // consumed last byte only partially or not at all
        {
            if (bitOffset > lastByteBits) // check if we didn't read more bits than available
            {
                throw new IOException("ByteArrayBitStreamReader: Unable to read bit on offset position " +
                        bitOffset + " in the last byte.");
            }
        }

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
            result = (byte)readBits(8);
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
            result = (short) readBits(16);
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
            result = (int)readBits(32);
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
            result = readBits(64);
        }
        return result;
    }

    @Override
    public BigInteger readBigInteger(final int numBits) throws IOException
    {
        BigInteger result = BigInteger.ZERO;
        int bitsToRead = numBits;
        if (bitsToRead > 8)
        {
            if (bitOffset != 0)
            {
                final int prefixLength = 8 - bitOffset;
                final long mostSignificantBits = readBits(prefixLength);
                result = BigInteger.valueOf(mostSignificantBits);
                bitsToRead -= prefixLength;
            }

            final int numBytes = bitsToRead / 8;
            final byte[] b = new byte[numBytes];
            readFully(b);
            final BigInteger i = new BigInteger(1, b);
            result = result.shiftLeft(8 * numBytes);
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
    public byte[] readBytes() throws IOException
    {
        final int length = readVarSize();
        final byte[] bytesValue = new byte[length];
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (int i = 0; i < length; ++i)
                bytesValue[i] = (byte)readBits(8);
        }
        else
        {
            // we are aligned to byte
            read(bytesValue, 0, length);
        }

        return bytesValue;
    }

    @Override
    public String readString() throws IOException
    {
        final int length = readVarSize();
        final byte[] readBuffer = new byte[length];
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (int i = 0; i < length; ++i)
                readBuffer[i] = (byte)readBits(8);
        }
        else
        {
            // we are aligned to byte
            read(readBuffer, 0, length);
        }

        return new String(readBuffer, DEFAULT_CHARSET_NAME);
    }

    @Override
    public boolean readBool() throws IOException
    {
        return readBits(1) == 1;
    }

    @Override
    public short readVarInt16() throws IOException
    {
        short b = (short)readBits(8); // byte 1
        final boolean sign = (b & VARINT_SIGN_1) != 0;
        short result = (short)(b & VARINT_BYTE_1);
        if ((b & VARINT_HAS_NEXT_1) == 0)
            return sign == true ? (short)-result : result;

        // byte 2
        result = (short)(result << 8 | readBits(8));
        return sign == true ? (short)-result : result;
    }

    @Override
    public int readVarInt32() throws IOException
    {
        int b = (int)readBits(8); // byte 1
        final boolean sign = (b & VARINT_SIGN_1) != 0;
        int result = b & VARINT_BYTE_1;
        if ((b & VARINT_HAS_NEXT_1) == 0)
            return sign == true ? -result : result;

        b = (int)readBits(8); // byte 2
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        b = (int)readBits(8); // byte 3
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return sign == true ? -result : result;

        // byte 4
        result = result << 8 | (int)readBits(8);
        return sign == true ? -result : result;
    }

    @Override
    public long readVarInt64() throws IOException
    {
        long b = readBits(8); // byte 1
        final boolean sign = (b & VARINT_SIGN_1) != 0;
        long result = b & VARINT_BYTE_1;
        if ((b & VARINT_HAS_NEXT_1) == 0)
            return sign == true ? -result : result;

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

        // byte 8
        result = result << 8 | readBits(8);
        return sign == true ? -result : result;
    }

    @Override
    public short readVarUInt16() throws IOException
    {
        short b = (short)readBits(8); // byte 1
        short result = (short)(b & VARUINT_BYTE);
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        // byte 2
        result = (short)(result << 8 | readBits(8));
        return result;
    }

    @Override
    public int readVarUInt32() throws IOException
    {
        int b = (int)readBits(8); // byte 1
        int result = b & VARUINT_BYTE;
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = (int)readBits(8); // byte 2
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = (int)readBits(8); // byte 3
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        // byte 4
        result = result << 8 | (int)readBits(8);
        return result;
    }

    @Override
    public long readVarUInt64() throws IOException
    {
        long b = readBits(8); // byte 1
        long result = b & VARUINT_BYTE;
        if ((b & VARUINT_HAS_NEXT) == 0)
            return result;

        b = readBits(8); // byte 2
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = readBits(8); // byte 3
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = readBits(8); // byte 4
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = readBits(8); // byte 5
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = readBits(8); // byte 6
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        b = readBits(8); // byte 7
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return result;

        // byte 8
        result = result << 8 | readBits(8);
        return result;
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
    public int readVarSize() throws IOException
    {
        long b = readBits(8); // byte 1
        long result = b & VARUINT_BYTE;
        if ((b & VARUINT_HAS_NEXT) == 0)
            return (int)result;

        b = readBits(8); // byte 2
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return (int)result;

        b = readBits(8); // byte 3
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return (int)result;

        b = readBits(8); // byte 4
        result = result << 7 | (b & VARINT_BYTE_N);
        if ((b & VARINT_HAS_NEXT_N) == 0)
            return (int)result;

        // byte 5
        result = result << 8 | (int)readBits(8);
        if (result > VARSIZE_MAX_VALUE)
            throw new IOException("ByteArrayBitStreamReader: Read value '" + result +
                    "' is out of range for varsize type!");

        return (int)result;
    }

    @Override
    public BitBuffer readBitBuffer() throws IOException
    {
        final int bitSize = readVarSize();
        final int numBytesToRead = bitSize / 8;
        final byte numRestBits = (byte)(bitSize - numBytesToRead * 8);
        final int byteSize = (bitSize + 7) / 8;
        final byte[] readBuffer = new byte[byteSize];
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (int i = 0; i < numBytesToRead; ++i)
                readBuffer[i] = (byte)readBits(8);
        }
        else
        {
            // we are aligned to byte
            read(readBuffer, 0, numBytesToRead);
        }

        if (numRestBits != 0)
            readBuffer[numBytesToRead] = (byte)(readBits(numRestBits) << (8 - numRestBits));

        return new BitBuffer(readBuffer, bitSize);
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
    public long getBufferBitSize()
    {
        return ((long)buffer.length) * 8 - 8 + lastByteBits;
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
     * Bit masks to mask appropriate bits during unaligned reading.
     */
    private static final int BIT_MASKS[] = { 0xff, 0x7f, 0x3f, 0x1f, 0x0f, 0x07, 0x03, 0x01 };

    /** Variable length integer sing bit mask for first byte. */
    private static final short VARINT_SIGN_1 = 0x80;
    /** Variable length integer value bit mask for first byte. */
    private static final short VARINT_BYTE_1 = 0x3f;
    /** Variable length integer value bit mask for intermediate bytes. */
    private static final short VARINT_BYTE_N = 0x7f;
    /** Variable length integer 'has next' bit mask for first byte. */
    private static final short VARINT_HAS_NEXT_1 = 0x40;
    /** Variable length integer 'has next' bit mask for intermediate bytes. */
    private static final short VARINT_HAS_NEXT_N = 0x80;

    /** Variable length integer value bit mask. */
    private static final short VARUINT_BYTE = 0x7f;
    /** Variable length integer 'has next' bit mask. */
    private static final short VARUINT_HAS_NEXT = 0x80;

    private static final long VARSIZE_MAX_VALUE = (1 << 31) - 1;

    /**
     * The underlying byte array.
     */
    private final byte[] buffer;
    private final byte lastByteBits;
}
