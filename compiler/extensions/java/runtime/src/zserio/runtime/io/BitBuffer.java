package zserio.runtime.io;

import java.nio.ByteBuffer;

import zserio.runtime.HashCodeUtil;

/**
 * Class which holds any bit sequence.
 *
 * Because bit buffer size does not have to be byte aligned (divisible by 8), it's possible that not all bits
 * of the last byte are used. In this case, only most significant bits of the corresponded size are used.
 */
public final class BitBuffer
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
