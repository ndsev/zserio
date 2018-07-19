package zserio.runtime.io;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

import zserio.runtime.Util;

/**
 * A bit stream writer using byte array.
 */
public class ByteArrayBitStreamWriter extends ByteArrayBitStreamBase implements BitStreamWriter
{
    /**
     * Constructs a new byte array bit stream writer with default capacity and the default endian byte order.
     */
    public ByteArrayBitStreamWriter()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_BYTE_ORDER);
    }

    /**
     * Constructs a new byte array bit stream writer with the default capacity and the given byte order.
     *
     * @param byteOrder The little endian or big endian byte order.
     */
    public ByteArrayBitStreamWriter(final ByteOrder byteOrder)
    {
        this(DEFAULT_INITIAL_CAPACITY, byteOrder);
    }

    /**
     * Constructs a new byte array bit stream writer with the given capacity and the default endian byte order.
     *
     * @param initialCapacity Underlying byte array capacity in bytes.
     */
    public ByteArrayBitStreamWriter(final int initialCapacity)
    {
        this(initialCapacity, DEFAULT_BYTE_ORDER);
    }

    /**
     * Constructs a new byte array bit stream writer with the given buffer capacity and the given byte order.
     *
     * @param initialCapacity Underlying byte array capacity in bytes.
     * @param byteOrder       The little endian or big endian byte order.
     */
    public ByteArrayBitStreamWriter(final int initialCapacity, final ByteOrder byteOrder)
    {
        super(byteOrder);
        if (initialCapacity < 0 || initialCapacity > MAX_BUFFER_SIZE)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Requested initial capacity " +
                    initialCapacity + " of underlying array is out of bounds [1, " + MAX_BUFFER_SIZE + "].");

        this.buffer = new byte[initialCapacity];
    }

    @Override
    public void writeBit(final int bit) throws IOException
    {
        writeBits(bit, 1);
    }

    @Override
    public void writeSignedBits(final long value, final int numBits) throws IOException
    {
        checkRange(numBits);

        if (numBits != BITS_PER_LONG)
        {
            final long lowerBound = Util.getBitFieldLowerBound(numBits, true);
            final long upperBound = Util.getBitFieldUpperBound(numBits, true);

            if (value < lowerBound || value > upperBound)
                throw new IllegalArgumentException("ByteArrayBitStreamWriter: Value " + value + " does not " +
                        "fit into " + numBits + " bits.");
        }
        // else: all values are OK

        writeBitsImpl(value, numBits);
    }

    @Override
    public void writeBits(final long value, final int numBits) throws IOException
    {
        // the MSB must be zero
        if (numBits <= 0 || numBits >= BITS_PER_LONG)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Number of written bits " + numBits +
                    " is out of range [1, " + BITS_PER_LONG + "].");

        final long lowerBound = 0;
        final long upperBound = Util.getBitFieldUpperBound(numBits, false);

        if (value < lowerBound || value > upperBound)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Written value " + value +
                    " does not fit into " + numBits + " bits.");

        writeBitsImpl(value, numBits);
    }

    @Override
    public void write(final int b) throws IOException
    {
        flushBits();
        ensureCapacity(BITS_PER_BYTE);
        buffer[bytePosition++] = (byte)b;
    }

    @Override
    public void write(final byte[] src) throws IOException
    {
        write(src, 0, src.length);
    }

    @Override
    public void write(final byte[] src, final int offset, final int length) throws IOException
    {
        flushBits();
        ensureCapacity(BITS_PER_BYTE * length);
        System.arraycopy(src, offset, buffer, bytePosition, length);
        this.bytePosition += length;
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException
    {
        write(v ? 1 : 0);
    }

    @Override
    public void writeByte(final int v) throws IOException
    {
        if (bitOffset == 0)
        {
            write((byte)v);
        }
        else
        {
            writeSignedBits((byte)v, BITS_PER_BYTE);
        }
    }

    @Override
    public void writeBytes(final String s) throws IOException
    {
        final byte[] bytes = s.getBytes(DEFAULT_CHARSET_NAME);
        for (final byte b : bytes)
        {
            writeByte(b);
        }
    }

    @Override
    public void writeChar(final int v) throws IOException
    {
        writeShort((char)v);
    }

    @Override
    public void writeChars(final String s) throws IOException
    {
        for (int i = 0; i < s.length(); i++)
        {
            writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeDouble(final double v) throws IOException
    {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeFloat(final float v) throws IOException
    {
        writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeShort(final int v) throws IOException
    {
        if (bitOffset == 0)
        {
            final byte b0 = (byte)v;
            final byte b1 = (byte)(v >> 8);

            ensureCapacity(BITS_PER_SHORT);
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                buffer[bytePosition++] = b1;
                buffer[bytePosition++] = b0;
            }
            else
            {
                buffer[bytePosition++] = b0;
                buffer[bytePosition++] = b1;
            }
        }
        else
        {
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                writeSignedBits((short)v, BITS_PER_SHORT);
            }
            else
            {
                writeSignedBits(Short.reverseBytes((short)v), BITS_PER_SHORT);
            }
        }
    }

    @Override
    public void writeInt(final int v) throws IOException
    {
        if (bitOffset == 0)
        {
            final byte b0 = (byte)v;
            final byte b1 = (byte)(v >> 8);
            final byte b2 = (byte)(v >> 16);
            final byte b3 = (byte)(v >> 24);

            ensureCapacity(BITS_PER_INT);
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                buffer[bytePosition++] = b3;
                buffer[bytePosition++] = b2;
                buffer[bytePosition++] = b1;
                buffer[bytePosition++] = b0;
            }
            else
            {
                buffer[bytePosition++] = b0;
                buffer[bytePosition++] = b1;
                buffer[bytePosition++] = b2;
                buffer[bytePosition++] = b3;
            }
        }
        else
        {
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                writeSignedBits(v, BITS_PER_INT);
            }
            else
            {
                writeSignedBits(Integer.reverseBytes(v), BITS_PER_INT);
            }
        }
    }

    @Override
    public void writeLong(final long v) throws IOException
    {
        if (bitOffset == 0)
        {
            final byte b0 = (byte)v;
            final byte b1 = (byte)(v >> 8);
            final byte b2 = (byte)(v >> 16);
            final byte b3 = (byte)(v >> 24);
            final byte b4 = (byte)(v >> 32);
            final byte b5 = (byte)(v >> 40);
            final byte b6 = (byte)(v >> 48);
            final byte b7 = (byte)(v >> 56);

            ensureCapacity(BITS_PER_LONG);
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                buffer[bytePosition++] = b7;
                buffer[bytePosition++] = b6;
                buffer[bytePosition++] = b5;
                buffer[bytePosition++] = b4;
                buffer[bytePosition++] = b3;
                buffer[bytePosition++] = b2;
                buffer[bytePosition++] = b1;
                buffer[bytePosition++] = b0;
            }
            else
            {
                buffer[bytePosition++] = b0;
                buffer[bytePosition++] = b1;
                buffer[bytePosition++] = b2;
                buffer[bytePosition++] = b3;
                buffer[bytePosition++] = b4;
                buffer[bytePosition++] = b5;
                buffer[bytePosition++] = b6;
                buffer[bytePosition++] = b7;
            }
        }
        else
        {
            if (byteOrder == ByteOrder.BIG_ENDIAN)
            {
                writeSignedBits(v, BITS_PER_LONG);
            }
            else
            {
                writeSignedBits(Long.reverseBytes(v), BITS_PER_LONG);
            }
        }
    }

    @Override
    public void writeUTF(final String s) throws IOException
    {
        throw new UnsupportedOperationException("ByteArrayBitStreamWriter: writeUTF() is unsupported.");
    }

    @Override
    public void close() throws IOException
    {
        /*
         * Silent method call.
         */
    }

    /**
     * Returns the underlying buffer content as byte array.
     *
     * @return A byte array containing the buffer values.
     *
     * @throws IOException If stream manipulation failed.
     */
    public byte[] toByteArray() throws IOException
    {
        final long bitPos = getBitPosition();
        flushBits();
        final byte[] dest = new byte[bytePosition];
        System.arraycopy(buffer, 0, dest, 0, dest.length);
        setBitPosition(bitPos);
        return dest;
    }

    @Override
    public void writeUnsignedByte(final short value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned byte. Value " +
                    value + " is negative.");

        writeBits(value, BITS_PER_BYTE);
    }

    @Override
    public void writeUnsignedShort(final int value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned short. Value " +
                    value + " is negative.");

        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            writeBits(value, BITS_PER_SHORT);
        }
        else
        {
            writeBits(Short.reverseBytes((short)value), BITS_PER_SHORT);
        }
    }

    @Override
    public void writeUnsignedInt(final long value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned integer. " +
                    "Value " + value + " is negative.");

        if (byteOrder == ByteOrder.BIG_ENDIAN)
        {
            writeBits(value, BITS_PER_INT);
        }
        else
        {
            writeBits(Integer.reverseBytes((int)value) & 0xffffffffL, BITS_PER_INT);
        }
    }

    /**
     * Writes given number of zero bits to the bit stream.
     *
     * @param count Number of zeros to write.
     *
     * @throws IOException If the bits cannot be written.
     */
    void writeZeros(int count) throws IOException
    {
        while (count > 0)
        {
            // write in BITS_PER_LONG - 1 chunks (maximum allowed by writeBits())
            final int bitsToWrite = Math.min(BITS_PER_LONG - 1, count);
            writeBits(0, bitsToWrite);
            count -= bitsToWrite;
        }
    }

    /**
     * Writes given number of one bits to the bit stream.
     *
     * @param count Number of ones to write.
     *
     * @throws IOException If the bits cannot be written.
     */
    void writeOnes(int count) throws IOException
    {
        while (count > 0)
        {
            // write in BITS_PER_LONG - 2 chunks to avoid overflow ( 1L << bitsToWrite must stay positive)
            final int bitsToWrite = Math.min(BITS_PER_LONG - 2, count);
            final long valueToWrite = (1L << bitsToWrite) - 1L;
            writeBits(valueToWrite, bitsToWrite);
            count -= bitsToWrite;
        }
    }

    @Override
    public void writeBigInteger(final BigInteger value, final int numBits) throws IOException
    {
        // total number of bits including sign bit for negative numbers
        final boolean isNegative = value.signum() < 0;
        final int valueBits = value.bitLength() + (isNegative ? +1 : 0);
        if (valueBits > numBits)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Written value " + value +
                    " does not fit into " + numBits + " bits.");

        if (valueBits < numBits)
        {
            final int paddingBits = numBits - valueBits;
            if (isNegative)
                writeOnes(paddingBits);
            else
                writeZeros(paddingBits);
        }

        final byte[] bytes = value.toByteArray();

        // only write the significant bits
        // (BigInteger.toByteArray() is happy to return a leading padding byte)
        int bitsToWrite = BITS_PER_BYTE - (BITS_PER_BYTE * bytes.length - valueBits);

        for (byte b : bytes)
        {
            if (bitsToWrite != 0)
                writeBits(((long)b) & 0xffL, bitsToWrite);

            bitsToWrite = BITS_PER_BYTE;
        }

    }

    @Override
    public void writeBool(final boolean value) throws IOException
    {
        writeBits(value ? 1 : 0, 1);
    }

    @Override
    public void writeVarInt16(final short value) throws IOException
    {
        writeVarNum(value, true, 2);
    }

    @Override
    public void writeVarUInt16(final short value) throws IOException
    {
        writeVarNum(value, false, 2);
    }

    @Override
    public void writeVarInt32(final int value) throws IOException
    {
        writeVarNum(value, true, 4);
    }

    @Override
    public void writeVarUInt32(final int value) throws IOException
    {
        writeVarNum(value, false, 4);
    }

    @Override
    public void writeVarInt64(final long value) throws IOException
    {
        writeVarNum(value, true, 8);
    }

    @Override
    public void writeVarUInt64(final long value) throws IOException
    {
        writeVarNum(value, false, 8);
    }

    @Override
    public void writeString(final String value) throws IOException
    {
        final byte[] bytes = value.getBytes(DEFAULT_CHARSET_NAME);
        writeVarUInt64((long)bytes.length);
        if (bitOffset == 0)
        {
            write(bytes);
        }
        else
        {
            for (final byte b : bytes)
            {
                writeByte(b);
            }
        }
    }

    @Override
    public void writeFloat16(final float value) throws IOException
    {
        writeUnsignedShort(floatToUInt16(value));
    }

    @Override
    public void skipBits(final int bitCnt) throws IOException
    {
        writeBits(0, bitCnt);
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

    /**
     * If the bit offset is non-zero, forces the remaining bits in the current byte to 0 and advances the stream
     * position by one.
     *
     * @exception IOException If some stream manipulation error occured.
     */
    protected final void flushBits() throws IOException
    {
        if (bitOffset != 0)
        {
            final int offset = bitOffset;
            int partialByte = read();
            if (partialByte < 0)
            {
                partialByte = 0;
                bitOffset = 0;
            }
            else
            {
                bytePosition--;
                partialByte &= -1 << (BITS_PER_BYTE - offset);
            }
            write(partialByte);
        }
    }

    /**
     * Writes a variable value with a given sign and the maximum number of variable bytes.
     *
     * @param value       Variable value to write.
     * @param signed      A flag indicating if the value is signed.
     * @param maxVarBytes The maximum number of variable bytes.
     *
     * @throws IOException If the bytes cannot be written.
     */
    private void writeVarNum(final long value, final boolean signed, final int maxVarBytes) throws IOException
    {
        if (value != 0)
        {
            final long absValue = (value < 0 ? -value : value);

            // checks if value is within varInt-type's range
            if (absValue > ((1L << (7 * maxVarBytes + (signed ? 0 : 1))) - 1))
                throw new IOException("ByteArrayBitStreamWriter: Can't write Var" + (signed ? "" : "U") +
                        "Int" + (8 * maxVarBytes) + ". Value " + value + " is out of range.");

            int numVarBytes = 0;
            int valBits = 0;
            for (int i = 0; i < maxVarBytes && (absValue > (1L << valBits) - 1); i++)
            {
                final int hasSign = i == 0 && signed ? 1 : 0;
                final int hasNextByte = i == maxVarBytes - 1 ? 0 : 1;
                valBits += BITS_PER_BYTE - (hasSign + hasNextByte);
                numVarBytes++;
            }

            final boolean max = numVarBytes == maxVarBytes;
            for (int i = 0; i < numVarBytes; i++)
            {
                final boolean hasNextByte = i < numVarBytes - 1;
                final int extra = max && hasNextByte ? 1 : 0;
                final int shift = (numVarBytes - (i + 1)) * 7 + extra;

                long b = 0;
                int numBits = BITS_PER_BYTE;
                if (signed && i == 0)
                {
                    b |= (value < 0 ? 1 : 0) << --numBits;
                }
                if (hasNextByte)
                {
                    b |= 1L << --numBits;
                }
                else if (!max)
                {
                    numBits--;
                }
                b |= (absValue >> shift) & (-1L >>> (BITS_PER_LONG - numBits));
                writeBits(b, BITS_PER_BYTE);
            }
        }
        else
        {
            writeByte(0);
        }
    }

    /**
     * Writes given number of bits of value to bit stream.
     *
     * @param value   Value to write.
     * @param numBits Number of bits of value to write.
     *
     * @throws IOException If the writing failed.
     */
    private void writeBitsImpl(final long value, final int numBits) throws IOException
    {
        ensureCapacity(numBits);

        /*
         * Write pre-existing bytes if we're not at the head of the buffer.
         */
        int nBits = numBits;
        if (bytePosition > 0 || bitOffset > 0)
        {
            final int initialOffset = bitOffset;
            int partialByte = read();
            bytePosition--;

            if (nBits + initialOffset < BITS_PER_BYTE)
            {
                final int shift = BITS_PER_BYTE - (initialOffset + nBits);
                final int mask = -1 >>> (BITS_PER_INT - nBits);
                partialByte &= ~(mask << shift);
                partialByte |= ((value & mask) << shift);
                buffer[bytePosition] = (byte)partialByte;
                bitOffset = initialOffset + nBits;
                nBits = 0;
            }
            else
            {
                final int sliceBits = BITS_PER_BYTE - initialOffset;
                final int mask = -1 >>> (BITS_PER_INT - sliceBits);
                partialByte &= ~mask;
                partialByte |= ((value >> (nBits - sliceBits)) & mask);
                buffer[bytePosition++] = (byte)partialByte;
                nBits -= sliceBits;
            }
        }

        /*
         * Write full bytes.
         */
        if (nBits >= BITS_PER_BYTE)
        {
            final int remaining = nBits & BYTE_MOD_MASK;
            for (int numBytes = nBits / BITS_PER_BYTE; numBytes > 0; numBytes--)
            {
                final int shift = (numBytes - 1) * BITS_PER_BYTE + remaining;
                final byte byteVal = (byte)((shift == 0 ? value : value >> shift) & 0xff);
                buffer[bytePosition++] = byteVal;
            }
            nBits = remaining;
        }

        /*
         * Write remaining bits.
         */
        if (nBits != 0)
        {
            int partialByte = read();
            final int shift = BITS_PER_BYTE - nBits;
            final int mask = -1 >>> (BITS_PER_INT - nBits);
            partialByte &= ~(mask << shift);
            partialByte |= (value & mask) << shift;
            buffer[--bytePosition] = (byte)partialByte;
            bitOffset = nBits;
        }
    }

    /**
     * Converts a given float value to an 16 bit unsigned integer value.
     *
     * @param value A float value to convert.
     *
     * @return Converted float value.
     */
    private static int floatToUInt16(final float value)
    {
        final int int32 = Float.floatToIntBits(value);
        final int sign = Math.abs((int32 & 0x80000000) >> 31);
        final int exp32 = (int32 & 0x7f800000) >> 23;
        final int m32 = int32 & 0x7fffff;
        int exp16 = 0;
        if (exp32 != 0)
        {
            if (exp32 == 0xff)
            {
                exp16 = 0x1f;
            }
            else
            {
                exp16 = exp32 - 127 + 15;
            }
        }
        int m16 = 0;
        if (exp16 < 0)
        {
            /*
             * +/- 0
             */
            exp16 = 0;
        }
        else if (exp16 > 0x1f)
        {
            /*
             * +/- Infinity
             */
            exp16 = 0x1f;
        }
        else
        {
            m16 = m32 >> 13;
        }
        return (sign << 15) | (exp16 << 10) | m16;
    }

    /**
     * Resets the bit offset and returns the next unsigned byte.
     *
     * @return Read next unsigned byte.
     *
     * @throws IOException If the reading failed.
     */
    private int read() throws IOException
    {
        bitOffset = 0;
        return nextUnsignedByte();
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
     * Returns the next byte without resetting the bit offset.
     *
     * @return Read next byte.
     *
     * @throws IOException If the reading failed.
     */
    private byte nextByte() throws IOException
    {
        if (bytePosition >= buffer.length)
            throw new EOFException("ByteArrayBitStreamWriter: Internal error, attemp to read beyond end of " +
                    "the buffer. Byte position is " + bytePosition + ". Buffer length is " + buffer.length +
                    ".");

        return buffer[bytePosition++];
    }

    /**
     * Increases the capacity of the underlying buffer to ensure that it can hold at least the given number of
     * bytes.
     *
     * @param minCapacity The desired minimum capacity.
     *
     * @throws OutOfMemoryError If the requested size exceeded the VM limit.
     */
    private void growBuffer(final int minCapacity)
    {
        final int oldCapacity = buffer.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
        {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_BUFFER_SIZE > 0)
        {
            if (minCapacity < 0)
                throw new OutOfMemoryError("ByteArrayBitStreamWriter: Reached maximum capacity of underlying " +
                        "buffer (" + MAX_BUFFER_SIZE + " bytes).");

            newCapacity = MAX_BUFFER_SIZE;
        }

        final byte[] newBuffer = new byte[newCapacity];
        System.arraycopy(buffer, 0, newBuffer, 0, oldCapacity);
        buffer = newBuffer;
    }

    /**
     * Method checks if the given next numBits can be stored in the buffer depending on the current byte
     * position. If the buffer is to small for the numBits the buffer is extended.
     *
     * @param numBits Number of bits as integer value to check.
     */
    private void ensureCapacity(final int numBits)
    {
        final int extraBits = numBits & BYTE_MOD_MASK;
        final int numBytes = (numBits / BITS_PER_BYTE);
        final int newPosition = bytePosition + numBytes + (extraBits > 0 ? 1 : 0);
        if (newPosition >= buffer.length - 1)
        {
            growBuffer(newPosition + 1);
        }
    }

    /**
     * The underlying byte array.
     */
    protected byte[] buffer;

    /**
     * The default initial buffer capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 8192;

    /**
     * The maximum size to allocate for a buffer. Some VMs reserve an additional header word in the underlying
     * array.
     */
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
}
