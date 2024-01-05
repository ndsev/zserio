package zserio.runtime.io;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;

/**
 * An interface for a bit stream reader implementation.
 */
public interface BitStreamReader extends Closeable
{
    /**
     * Reads the next numBits as signed bit value.
     *
     * @param numBits Number of bits to read.
     *
     * @return Read signed bit value.
     *
     * @throws IOException If reading failed.
     */
    long readSignedBits(final int numBits) throws IOException;

    /**
     * Reads the next numBits of the bit stream as long value.
     *
     * @param numBits Number of bits to read.
     *
     * @return Read numBits value.
     *
     * @throws IOException If reading failed.
     */
    long readBits(final int numBits) throws IOException;

    /**
     * Reads the next signed byte (8 bits).
     *
     * @return Read signed byte value.
     *
     * @throws IOException If reading failed.
     */
    byte readByte() throws IOException;

    /**
     * Reads the next unsigned byte (8 bits) as short value.
     *
     * @return Read unsigned byte value.
     *
     * @throws IOException If reading failed.
     */
    short readUnsignedByte() throws IOException;

    /**
     * Reads the next signed short (16 bits).
     *
     * @return Read signed short value.
     *
     * @throws IOException If reading failed.
     */
    short readShort() throws IOException;

    /**
     * Read the next unsigned short (16 bits) as int value.
     *
     * @return Read unsigned short value.
     *
     * @throws IOException If reading failed.
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads the next signed integer (32 bits).
     *
     * @return Read signed integer value.
     *
     * @throws IOException If reading failed.
     */
    int readInt() throws IOException;

    /**
     * Reads the next unsigned integer (32 bits) as long value.
     *
     * @return Read unsigned integer value.
     *
     * @throws IOException If reading failed.
     */
    long readUnsignedInt() throws IOException;

    /**
     * Reads the next signed long (64 bits).
     *
     * @return Read signed long value.
     *
     * @throws IOException If reading failed.
     */
    long readLong() throws IOException;

    /**
     * Reads the next unsigned Big Integer value with containing numBits bits.
     *
     * @param numBits Number of bits to read.
     *
     * @return Read big integer with the given bits.
     *
     * @throws IOException If reading failed.
     */
    BigInteger readBigInteger(final int numBits) throws IOException;

    /**
     * Reads the next signed big Integer value with containing numBits bits.
     *
     * @param numBits Number of bits to read.
     *
     * @return Read big integer with the given bits.
     *
     * @throws IOException If reading failed.
     */
    BigInteger readSignedBigInteger(final int numBits) throws IOException;

    /**
     * Reads the a float16 value from the bit stream.
     *
     * @return Read half precision float value.
     *
     * @throws IOException If reading failed.
     */
    float readFloat16() throws IOException;

    /**
     * Reads the a float32 value from the bit stream.
     *
     * @return Read single precision float value.
     *
     * @throws IOException If reading failed.
     */
    float readFloat32() throws IOException;

    /**
     * Reads the a float64 value from the bit stream.
     *
     * @return Read double precision float value.
     *
     * @throws IOException If reading failed.
     */
    double readFloat64() throws IOException;

    /**
     * Reads a Zserio bytes from the bit stream.
     *
     * @return Read Zserio bytes as byte[].
     *
     * @throws IOException If reading failed.
     */
    byte[] readBytes() throws IOException;

    /**
     * Reads a Zserio string from the bit stream assuming it is encoded in UTF-8.
     *
     * @return Read Zserio string.
     *
     * @throws IOException If reading failed.
     */
    String readString() throws IOException;

    /**
     * Reads a Boolean value from the bit stream.
     *
     * @return Read boolean value.
     *
     * @throws IOException If reading failed.
     */
    boolean readBool() throws IOException;

    /**
     * Reads a variable 16 bit integer from the bit stream.
     *
     * @return Read VarInt16 value.
     *
     * @throws IOException If reading failed.
     */
    short readVarInt16() throws IOException;

    /**
     * Reads a variable 32 bit integer from the bit stream.
     *
     * @return Read VarInt32 value.
     *
     * @throws IOException If reading failed.
     */
    int readVarInt32() throws IOException;

    /**
     * Reads a variable 64 bit integer from the bit stream.
     *
     * @return Read VarInt64 value.
     *
     * @throws IOException If reading failed.
     */
    long readVarInt64() throws IOException;

    /**
     * Reads an unsigned variable 16 bit integer from the bit stream.
     *
     * @return Read unsigned VarUInt16 value.
     *
     * @throws IOException If reading failed.
     */
    short readVarUInt16() throws IOException;

    /**
     * Reads an unsigned variable 32 bit integer from the bit stream.
     *
     * @return Read unsigned VarUInt32 value.
     *
     * @throws IOException If reading failed.
     */
    int readVarUInt32() throws IOException;

    /**
     * Reads an unsigned variable 64 bit integer from the bit stream.
     *
     * @return Read unsigned VarUInt64 value.
     *
     * @throws IOException If reading failed.
     */
    long readVarUInt64() throws IOException;

    /**
     * Reads a signed variable integer from the bit stream.
     *
     * The integer takes up to 9 bytes to cover range &lt;-2^63, 2^63-1&gt;.
     *
     * @return Read signed VarInt value.
     *
     * @throws IOException If reading failed.
     */
    long readVarInt() throws IOException;

    /**
     * Reads an unsigned variable integer from the bit stream.
     *
     * The integer takes up to 9 bytes to cover range &lt;0, 2^64-1&gt;.
     *
     * @return Read unsigned VarUInt value.
     *
     * @throws IOException If reading failed.
     */
    BigInteger readVarUInt() throws IOException;

    /**
     * Reads a variable size integer from the bit stream.
     *
     * @return Read VarSize value.
     *
     * @throws IOException If reading failed.
     */
    int readVarSize() throws IOException;

    /**
     * Reads a bit buffer from the bit stream.
     *
     * @return Read bit buffer.
     *
     * @throws IOException If reading failed.
     */
    BitBuffer readBitBuffer() throws IOException;

    /**
     * Gets the current bit position.
     *
     * @return Current bit position counted from zero.
     */
    long getBitPosition();

    /**
     * Gets the current byte position.
     *
     * @return Current byte position counted from zero.
     */
    int getBytePosition();

    /**
     * Sets the bit position to the given value.
     *
     * @param position Bit position counted from zero to set.
     *
     * @throws IOException If the position cannot be set.
     */
    void setBitPosition(long position) throws IOException;

    /**
     * Aligns the bit position according to the aligning value.
     *
     * @param alignVal An aligning value to use.
     *
     * @throws IOException If the alignment failed.
     */
    void alignTo(final int alignVal) throws IOException;

    /**
     * Gets size of the underlying buffer in bits.
     *
     * @return Buffer bit size.
     */
    long getBufferBitSize();
}
