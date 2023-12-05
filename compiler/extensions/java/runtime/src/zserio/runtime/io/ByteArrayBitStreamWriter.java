package zserio.runtime.io;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.FloatUtil;
import zserio.runtime.BitFieldUtil;
import zserio.runtime.ZserioError;
import zserio.runtime.VarSizeUtil;

/**
 * A bit stream writer using byte array.
 */
public final class ByteArrayBitStreamWriter extends ByteArrayBitStreamBase implements BitStreamWriter
{
    /**
     * Constructs a new byte array bit stream writer with default capacity and the default endian byte order.
     */
    public ByteArrayBitStreamWriter()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new byte array bit stream writer with the given buffer capacity.
     *
     * @param initialCapacity Underlying byte array capacity in bytes.
     */
    public ByteArrayBitStreamWriter(final int initialCapacity)
    {
        if (initialCapacity < 0 || initialCapacity > MAX_BUFFER_SIZE)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Requested initial capacity " +
                    initialCapacity + " of underlying array is out of bounds [1, " + MAX_BUFFER_SIZE + "].");

        this.buffer = new byte[initialCapacity];
    }

    @Override
    public void writeSignedBits(final long value, final int numBits) throws IOException
    {
        checkRange(numBits);

        if (numBits != 64)
        {
            final long lowerBound = BitFieldUtil.getBitFieldLowerBound(numBits, true);
            final long upperBound = BitFieldUtil.getBitFieldUpperBound(numBits, true);

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
        if (numBits <= 0 || numBits >= 64)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Number of written bits " + numBits +
                    " is out of range [1, 64].");

        final long lowerBound = 0;
        final long upperBound = BitFieldUtil.getBitFieldUpperBound(numBits, false);

        if (value < lowerBound || value > upperBound)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Written value " + value +
                    " does not fit into " + numBits + " bits.");

        writeBitsImpl(value, numBits);
    }

    @Override
    public void writeByte(final byte v) throws IOException
    {
        if (bitOffset == 0)
        {
            write(v);
        }
        else
        {
            writeSignedBits(v, 8);
        }
    }

    @Override
    public void writeUnsignedByte(final short value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned byte. Value " +
                    value + " is negative.");

        writeBits(value, 8);
    }

    @Override
    public void writeShort(final short v) throws IOException
    {
        if (bitOffset == 0)
        {
            final byte b0 = (byte)v;
            final byte b1 = (byte)(v >> 8);

            ensureCapacity(16);

            buffer[bytePosition++] = b1;
            buffer[bytePosition++] = b0;
        }
        else
        {
            writeSignedBits(v, 16);
        }
    }

    @Override
    public void writeUnsignedShort(final int value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned short. Value " +
                    value + " is negative.");

        writeBits(value, 16);
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

            ensureCapacity(32);

            buffer[bytePosition++] = b3;
            buffer[bytePosition++] = b2;
            buffer[bytePosition++] = b1;
            buffer[bytePosition++] = b0;
        }
        else
        {
            writeSignedBits(v, 32);
        }
    }

    @Override
    public void writeUnsignedInt(final long value) throws IOException
    {
        if (value < 0)
            throw new IllegalArgumentException("ByteArrayBitStreamWriter: Can't write unsigned integer. " +
                    "Value " + value + " is negative.");

        writeBits(value, 32);
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

            ensureCapacity(64);

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
            writeSignedBits(v, 64);
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
        int bitsToWrite = 8 - (8 * bytes.length - valueBits);

        for (byte b : bytes)
        {
            if (bitsToWrite != 0)
                writeBitsImpl(((long)b) & 0xffL, bitsToWrite);

            bitsToWrite = 8;
        }
    }

    @Override
    public void writeFloat16(final float value) throws IOException
    {
        writeShort(FloatUtil.convertFloatToShort(value));
    }

    @Override
    public void writeFloat32(final float value) throws IOException
    {
        writeInt(FloatUtil.convertFloatToInt(value));
    }

    @Override
    public void writeFloat64(final double value) throws IOException
    {
        writeLong(FloatUtil.convertDoubleToLong(value));
    }

    @Override
    public void writeBytes(final byte[] value) throws IOException
    {
        writeVarSize(value.length);
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (byte b : value)
                writeBitsImpl(b, 8);
        }
        else
        {
            // we are aligned to byte
            write(value);
        }
    }

    @Override
    public void writeString(final String value) throws IOException
    {
        final byte[] bytes = value.getBytes(DEFAULT_CHARSET_NAME);
        writeVarSize(bytes.length);
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (byte b : bytes)
                writeBitsImpl(b, 8);
        }
        else
        {
            // we are aligned to byte
            write(bytes);
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
        try
        {
            writeVarNum(value, true, 2, BitSizeOfCalculator.getBitSizeOfVarInt16(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarUInt16(final short value) throws IOException
    {
        try
        {
            writeVarNum(value, false, 2, BitSizeOfCalculator.getBitSizeOfVarUInt16(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarInt32(final int value) throws IOException
    {
        try
        {
            writeVarNum(value, true, 4, BitSizeOfCalculator.getBitSizeOfVarInt32(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarUInt32(final int value) throws IOException
    {
        try
        {
            writeVarNum(value, false, 4, BitSizeOfCalculator.getBitSizeOfVarUInt32(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarInt64(final long value) throws IOException
    {
        try
        {
            writeVarNum(value, true, 8, BitSizeOfCalculator.getBitSizeOfVarInt64(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarUInt64(final long value) throws IOException
    {
        try
        {
            writeVarNum(value, false, 8, BitSizeOfCalculator.getBitSizeOfVarUInt64(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeVarInt(final long value) throws IOException
    {
        if (value == Long.MIN_VALUE)
        {
            writeByte(VARINT_MIN_VALUE);
        }
        else
        {
            try
            {
                writeVarNum(value, true, 9, BitSizeOfCalculator.getBitSizeOfVarInt(value) / 8);
            }
            catch (ZserioError e)
            {
                throw new IOException(e.getMessage());
            }
        }
    }

    @Override
    public void writeVarUInt(final BigInteger value) throws IOException
    {
        int numBytes = 0;
        try
        {
            // contains validity check
            numBytes = BitSizeOfCalculator.getBitSizeOfVarUInt(value) / 8;
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }

        final int extraShift = numBytes == VARUINT_MAX_BYTES ? 1 : 0;
        int shift = (numBytes - 1) * 7;

        // first byte
        writeBool(numBytes > 1); // has next byte
        writeBits(value.shiftRight(shift + extraShift).and(VARUINT_BITMASK).longValue(), 7);

        // middle bytes
        for (int i = numBytes - 1; i > 1; i--)
        {
            shift = (i - 1) * 7;
            writeBool(true); // has next byte
            writeBits(value.shiftRight(shift + extraShift).and(VARUINT_BITMASK).longValue(), 7);
        }

        // last byte
        if (numBytes > 1)
        {
            if (numBytes == VARUINT_MAX_BYTES)
            {
                // last possible byte of varuint doesn't have the "has next byte" bit
                writeBits(value.and(VARUINT_9TH_BITMASK).longValue(), 8);
            }
            else
            {
                writeBool(false); // has next byte
                writeBits(value.and(VARUINT_BITMASK).longValue(), 7);
            }
        }
    }

    @Override
    public void writeVarSize(final int value) throws IOException
    {
        try
        {
            writeVarNum(value, false, 5, BitSizeOfCalculator.getBitSizeOfVarSize(value) / 8);
        }
        catch (ZserioError e)
        {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void writeBitBuffer(final BitBuffer bitBuffer) throws IOException
    {
        final long bitSize = bitBuffer.getBitSize();
        writeVarSize(VarSizeUtil.convertBitBufferSizeToInt(bitSize));

        final byte[] writeBuffer = bitBuffer.getBuffer();
        final int numBytesToWrite = (int)(bitSize / 8);
        final byte numRestBits = (byte)(bitSize - (long)numBytesToWrite * 8);
        if (bitOffset != 0)
        {
            // we are not aligned to byte
            for (int i = 0; i < numBytesToWrite; ++i)
                writeBitsImpl(writeBuffer[i], 8);
        }
        else
        {
            // we are aligned to byte
            write(writeBuffer, 0, numBytesToWrite);
        }

        if (numRestBits > 0)
            writeBitsImpl(writeBuffer[numBytesToWrite] >> (8 - numRestBits), numRestBits);
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

    private void skipBits(final int bitCnt) throws IOException
    {
        writeBits(0, bitCnt);
    }

    private void write(final int b) throws IOException
    {
        flushBits();
        ensureCapacity(8);
        buffer[bytePosition++] = (byte)b;
    }

    private void write(final byte[] src) throws IOException
    {
        write(src, 0, src.length);
    }

    private void write(final byte[] src, final int offset, final int length) throws IOException
    {
        flushBits();
        ensureCapacity(8 * length);
        System.arraycopy(src, offset, buffer, bytePosition, length);
        this.bytePosition += length;
    }

    /**
     * Writes given number of zero bits to the bit stream.
     *
     * @param count Number of zeros to write.
     *
     * @throws IOException If the bits cannot be written.
     */
    private void writeZeros(int count) throws IOException
    {
        while (count > 0)
        {
            // write in 64 - 1 chunks (maximum allowed by writeBits())
            final int bitsToWrite = Math.min(64 - 1, count);
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
    private void writeOnes(int count) throws IOException
    {
        while (count > 0)
        {
            // write in 64 - 2 chunks to avoid overflow ( 1L << bitsToWrite must stay positive)
            final int bitsToWrite = Math.min(64 - 2, count);
            final long valueToWrite = (1L << bitsToWrite) - 1L;
            writeBits(valueToWrite, bitsToWrite);
            count -= bitsToWrite;
        }
    }

    /**
     * If the bit offset is non-zero, forces the remaining bits in the current byte to 0 and advances the stream
     * position by one.
     *
     * @exception IOException If some stream manipulation error occurred.
     */
    private final void flushBits() throws IOException
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
                partialByte &= -1 << (8 - offset);
            }
            write(partialByte);
        }
    }

    /**
     * Writes a variable value with a given sign and the maximum number of variable bytes.
     *
     * @param value       Variable value to write.
     * @param isSigned    A flag indicating if the value is signed.
     * @param maxVarBytes The maximum number of variable bytes.
     * @param numVarBytes The number of variable bytes.
     *
     * @throws IOException If the bytes cannot be written.
     */
    private void writeVarNum(final long value, final boolean isSigned, final int maxVarBytes,
            final int numVarBytes) throws IOException
    {
        final long absValue = (value < 0 ? -value : value);
        final boolean max = numVarBytes == maxVarBytes;
        for (int i = 0; i < numVarBytes; i++)
        {
            final boolean hasNextByte = i < numVarBytes - 1;
            final int extra = max && hasNextByte ? 1 : 0;
            final int shift = (numVarBytes - (i + 1)) * 7 + extra;

            long b = 0;
            int numBits = 8;
            if (isSigned && i == 0)
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
            b |= (absValue >> shift) & (-1L >>> (64 - numBits));
            writeBits(b, 8);
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

            if (nBits + initialOffset < 8)
            {
                final int shift = 8 - (initialOffset + nBits);
                final int mask = -1 >>> (32 - nBits);
                partialByte &= ~(mask << shift);
                partialByte |= ((value & mask) << shift);
                buffer[bytePosition] = (byte)partialByte;
                bitOffset = initialOffset + nBits;
                nBits = 0;
            }
            else
            {
                final int sliceBits = 8 - initialOffset;
                final int mask = -1 >>> (32 - sliceBits);
                partialByte &= ~mask;
                partialByte |= ((value >> (nBits - sliceBits)) & mask);
                buffer[bytePosition++] = (byte)partialByte;
                nBits -= sliceBits;
            }
        }

        /*
         * Write full bytes.
         */
        if (nBits >= 8)
        {
            final int remaining = nBits & BYTE_MOD_MASK;
            for (int numBytes = nBits / 8; numBytes > 0; numBytes--)
            {
                final int shift = (numBytes - 1) * 8 + remaining;
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
            final int shift = 8 - nBits;
            final int mask = -1 >>> (32 - nBits);
            partialByte &= ~(mask << shift);
            partialByte |= (value & mask) << shift;
            buffer[--bytePosition] = (byte)partialByte;
            bitOffset = nBits;
        }
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
        final int numBytes = (numBits / 8);
        final int newPosition = bytePosition + numBytes + (extraBits > 0 ? 1 : 0);
        if (newPosition >= buffer.length - 1)
        {
            growBuffer(newPosition + 1);
        }
    }

    /**
     * The underlying byte array.
     */
    private byte[] buffer;

    /**
     * The default initial buffer capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 8192;

    /**
     * The maximum size to allocate for a buffer. Some VMs reserve an additional header word in the underlying
     * array.
     */
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    /** Minimum VarInt value is Long.MIN_VALUE but it is encoded as -0. */
    private static final byte VARINT_MIN_VALUE = (byte)0x80;

    /** Maximum number of bytes needed to encode VarUInt. */
    private static final int VARUINT_MAX_BYTES = 9;

    /** Bitmask for value in VarUInt byte (except of the 9th byte). */
    private static final BigInteger VARUINT_BITMASK = BigInteger.valueOf(0x7F);
    /** Bitmask for value in VarUInt's 9th byte. */
    private static final BigInteger VARUINT_9TH_BITMASK = BigInteger.valueOf(0xFF);
}
