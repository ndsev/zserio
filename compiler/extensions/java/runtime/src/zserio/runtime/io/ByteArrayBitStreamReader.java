package zserio.runtime.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * A bit stream reader using byte array.
 */
public class ByteArrayBitStreamReader extends ByteArrayBitStreamBase implements BitStreamReader
{
    /**
     * Constructs object containing given bytes.
     *
     * @param bytes Array of bytes to construct from.
     */
    public ByteArrayBitStreamReader(final byte[] bytes)
    {
        this(bytes, DEFAULT_BYTE_ORDER);
    }

    /**
     * Constructs object containing given bytes with a given byte order.
     *
     * @param bytes     Array of bytes to construct from.
     * @param byteOrder Little endian or big endian byte order.
     */
    public ByteArrayBitStreamReader(final byte[] bytes, final ByteOrder byteOrder)
    {
        super(byteOrder);
        this.buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
    }

    /**
     * Constructs object containing given byte order.
     *
     * @param byteOrder Little endian or big endian byte order.
     */
    protected ByteArrayBitStreamReader(ByteOrder byteOrder)
    {
        super(byteOrder);
    }

    @Override
    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }

    @Override
    public long readBits(final int numBits) throws IOException
    {
        /*
         * If unaligned, we can only read 7 bytes at a time.
         * Maybe we should adapt the range check to deal with this.
         */
        checkRange(numBits);

        int bitsToRead = bitOffset + numBits;
        final int nextBitOffset = bitsToRead & BYTE_MOD_MASK;

        long accum = 0L;
        while (bitsToRead > 0)
        {
            accum = (accum << BITS_PER_BYTE) | nextUnsignedByte();
            bitsToRead -= BITS_PER_BYTE;
        }

        bitOffset = nextBitOffset;
        if (nextBitOffset != 0)
        {
            bytePosition--;
        }

        /*
         * Shift away unwanted bits on the right and mask out unwanted bits on
         * the left.
         */
        return (accum >>> -bitsToRead) & (-1L >>> (BITS_PER_LONG - numBits));
    }

    /**
     * Resets the bit offset and returns the next unsigned byte.
     *
     * @return Read next unsigned byte.
     *
     * @throws IOException If the reading failed.
     */
    public int read() throws IOException
    {
        bitOffset = 0;
        return nextUnsignedByte();
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
    public int readUnsignedByte() throws IOException
    {
        return readByte() & 0xff;
    }

    @Override
    public short readShort() throws IOException
    {
        short result;
        if (bitOffset == 0)
        {
            final byte b0 = nextByte();
            final byte b1 = nextByte();
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                result = makeShort(b0, b1);
            }
            else
            {
                result = makeShort(b1, b0);
            }
        }
        else
        {
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                result = (short) readBits(BITS_PER_SHORT);
            }
            else
            {
                result = Short.reverseBytes((short)readBits(BITS_PER_SHORT));
            }
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
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                result = makeInt(b0, b1, b2, b3);
            }
            else
            {
                result = makeInt(b3, b2, b1, b0);
            }
        }
        else
        {
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                result = (int)readBits(BITS_PER_INT);
            }
            else
            {
                result = Integer.reverseBytes((int)readBits(BITS_PER_INT));
            }
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
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                result = makeLong(b0, b1, b2, b3, b4, b5, b6, b7);
            }
            else
            {
                result = makeLong(b7, b6, b5, b4, b3, b2, b1, b0);
            }
        }
        else
        {
            result = readBits(BITS_PER_BYTE) << BITS_PER_7_BYTES;
            result |= readBits(BITS_PER_7_BYTES);
            if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            {
                result = Long.reverseBytes(result);
            }
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
        final int uint16 = readUnsignedShort();
        return uint16ToFloat(uint16);
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
    public void skipBits(final int bitCnt) throws IOException
    {
        setBitPosition(getBitPosition() + bitCnt);
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
    public boolean readBoolean() throws IOException
    {
        return readUnsignedByte() > 0;
    }

    @Override
    public char readChar() throws IOException
    {
        return (char)readShort();
    }

    @Override
    public double readDouble() throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public float readFloat() throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public void readFully(final byte[] b) throws IOException
    {
        readFully(b, 0, b.length);
    }

    @Override
    public void readFully(final byte[] dest, final int offset, final int length) throws IOException
    {
        int count = read(dest, offset, length);
        if (count != length)
            throw new EOFException("ByteArrayBitStreamReader: Wrong number of read bytes. Read was " + count +
                    " but requested was " + length + ".");
    }

    @Override
    public int read(final byte[] dest, final int offset, final int length)
    {
        System.arraycopy(buffer, bytePosition, dest, offset, length);
        bytePosition += length;
        return length;
    }

    @Override
    public String readLine() throws IOException
    {
        throw new UnsupportedOperationException("ByteArrayBitStreamReader: readLine() is unsupported.");
    }

    @Override
    public String readUTF() throws IOException
    {
        throw new UnsupportedOperationException("ByteArrayBitStreamReader: readUTF() is unsupported.");
    }

    @Override
    public int skipBytes(final int n) throws IOException
    {
        setBitPosition(getBitPosition() + (long)n * BITS_PER_BYTE);
        return n;
    }

    @Override
    public void close() throws IOException
    {
        /*
         * Silent method call.
         */
    }

    @Override
    public long readBit() throws IOException
    {
        return readBits(1);
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
     * Converts a 16 bit unsigned integer into an 16 bit unsigned float value
     *
     * @param uint16 An 16 bit unsigned integer value.
     *
     * @return An 16 bit unsigned float value.
     */
    private static float uint16ToFloat(final int uint16)
    {
        final int sign = (uint16 & 0x8000) >> 15;
        final int exp16 = (uint16 & 0x7c00) >> 10;
        final int m16 = uint16 & 0x3ff;
        int exp32 = 0;
        if (exp16 != 0) {
            if (exp16 == 0x1f) {
                exp32 = 0xff;
            } else {
                exp32 = exp16 - 15 + 127;
            }
        }
        final int bits = (sign << 31) | (exp32 << 23) | (m16 << 13);
        return Float.intBitsToFloat(bits);
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
     * The number of bits per 7 bytes.
     */
    protected static final int BITS_PER_7_BYTES = 0x38;

    /**
     * The underlying byte array.
     */
    protected byte[] buffer;
}
