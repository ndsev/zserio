package zserio.runtime.io;

import java.nio.ByteBuffer;

import zserio.runtime.HashCodeUtil;

/**
 * Class which holds any bit sequence.
 *
 * Because bit buffer size does not have to be byte aligned (divisible by 8), it's possible that not all bits
 * of the last byte are used. In this case, only most significant bits of the corresponded size are used.
 */
public final class BitBuffer implements Comparable<BitBuffer>
{
    /**
     * Constructor from byte buffer.
     *
     * All bits of the last buffer byte will be used.
     *
     * @param buffer Byte buffer to construct from.
     */
    public BitBuffer(byte[] buffer)
    {
        this(buffer, (long)buffer.length * 8);
    }

    /**
     * Constructor from byte buffer and bit size.
     *
     * @param buffer  Byte buffer to construct from.
     * @param bitSize Number of bits stored in buffer to use.
     *
     * @throws IllegalArgumentException If the number of bits is bigger than buffer size.
     */
    public BitBuffer(byte[] buffer, long bitSize) throws IllegalArgumentException
    {
        final int byteSize = (int)((bitSize + 7) / 8);
        if (buffer.length < byteSize)
            throw new IllegalArgumentException("BitBuffer: Bit size " + bitSize +
                    " out of range for given buffer byte size " + buffer.length + "!");

        this.buffer = new byte[byteSize];
        System.arraycopy(buffer, 0, this.buffer, 0, byteSize);
        this.bitSize = bitSize;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BitBuffer))
            return false;

        final BitBuffer other = (BitBuffer)obj;

        if (bitSize != other.bitSize)
            return false;

        final int byteSize = getByteSize();
        if (byteSize > 0)
        {
            if (byteSize > 1)
            {
                final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, byteSize - 1);
                final ByteBuffer otherByteBuffer = ByteBuffer.wrap(other.buffer, 0, byteSize - 1);
                if (!byteBuffer.equals(otherByteBuffer))
                    return false;
            }

            if (getMaskedLastByte() != other.getMaskedLastByte())
                return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = HashCodeUtil.HASH_SEED;

        final int byteSize = getByteSize();
        if (byteSize > 0)
        {
            if (byteSize > 1)
            {
                for (int i = 0; i < byteSize - 1; ++i)
                    result = HashCodeUtil.calcHashCode(result, buffer[i]);
            }
            result = HashCodeUtil.calcHashCode(result, getMaskedLastByte());
        }

        return result;
    }

    /**
     * Compares this bit buffer with the other bit buffer.
     *
     * Compares byte by byte using lexicographical compare of unsigned bytes, the last byte is masked to use
     * only the proper number of bits. Bit buffers with equal contents are ordered by their bit size.
     *
     * @param other The other bit buffer to compare with.
     *
     * @return Negative integer, zero, or a positive integer as this bit buffer is less than, equal to,
     *         or greater than the other bit buffer.
     */
    @Override
    public int compareTo(BitBuffer other)
    {
        final int byteSize1 = getByteSize();
        final int byteSize2 = other.getByteSize();
        if (byteSize1 == 0 || byteSize2 == 0)
            return Integer.compare(byteSize1, byteSize2);

        final int lastIndex1 = byteSize1 - 1;
        final int lastIndex2 = byteSize2 - 1;
        int index = 0;
        for (; index != lastIndex1 && index != lastIndex2; ++index)
        {
            final int result = Integer.compare(buffer[index] & 0xFF, other.buffer[index] & 0xFF);
            if (result != 0)
                return result;
        }

        final int lastValue1 = (index != lastIndex1 ? buffer[index] : getMaskedLastByte()) & 0xFF;
        final int lastValue2 = (index != lastIndex2 ? other.buffer[index] : other.getMaskedLastByte()) & 0xFF;
        final int result = Integer.compare(lastValue1, lastValue2);
        if (result != 0)
            return result;

        if (index == lastIndex1 && index == lastIndex2)
            return Long.compare(bitSize, other.bitSize);

        return (index == lastIndex1) ? -1 : 1;
    }

    /**
     * Gets the underlying byte buffer.
     *
     * Not all bits of the last byte must be used.
     *
     * @return The underlying byte buffer.
     */
    public byte[] getBuffer()
    {
        final byte[] bufferCopy = new byte[getByteSize()];
        System.arraycopy(buffer, 0, bufferCopy, 0, bufferCopy.length);

        return bufferCopy;
    }

    /**
     * Gets the number of bits stored in the bit buffer.
     *
     * @return Size of the bit buffer in bits.
     */
    public long getBitSize()
    {
        return bitSize;
    }

    /**
     * Gets the number of bytes stored in the bit buffer.
     *
     * @return Size of the bit buffer in bytes.
     */
    public int getByteSize()
    {
        return (int)((bitSize + 7) / 8);
    }

    private byte getMaskedLastByte()
    {
        final int roundedByteSize = (int)(bitSize / 8);
        final byte lastByteBits = (byte)(bitSize - (long)8 * roundedByteSize);

        return (lastByteBits == 0)
                ? buffer[roundedByteSize - 1]
                : (byte)(buffer[roundedByteSize] & (0xFF << (8 - lastByteBits)));
    }

    private final byte[] buffer;
    private final long bitSize;
}
