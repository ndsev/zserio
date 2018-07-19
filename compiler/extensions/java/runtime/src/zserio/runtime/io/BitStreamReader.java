package zserio.runtime.io;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * An interface for a bit stream reader implementation.
 */
public interface BitStreamReader extends BitStreamConsumer, DataInput
{
    /**
     * Gets the byte order.
     *
     * @return Used byte order.
     */
    ByteOrder getByteOrder();

    /**
     * Reads the next bit of the bit stream as long value.
     *
     * @return Read bit value.
     *
     * @throws IOException if the reading fails
     */
    long readBit() throws IOException;

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
     * Reads the next unsigned integer as long value.
     *
     * @return Read unsigned integer value.
     *
     * @throws IOException If reading failed.
     */
    long readUnsignedInt() throws IOException;

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
     * @return Read Float16 value.
     *
     * @throws IOException If reading failed.
     */
    float readFloat16() throws IOException;

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
     * @return Read unsigned VarInt16 value.
     *
     * @throws IOException If reading failed.
     */
    short readVarUInt16() throws IOException;

    /**
     * Reads an unsigned variable 32 bit integer from the bit stream.
     *
     * @return Read unsigned VarInt32 value.
     *
     * @throws IOException If reading failed.
     */
    int readVarUInt32() throws IOException;

    /**
     * Reads an unsigned variable 64 bit integer from the bit stream.
     *
     * @return Read unsigned VarInt64 value.
     *
     * @throws IOException If reading failed.
     */
    long readVarUInt64() throws IOException;

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
     * Reads given number of bytes to given byte array.
     *
     * @param dest   Byte array where to store read bytes.
     * @param offset Offset to byte array <code>dest</code> where to start storing of read bytes.
     * @param length Number of bytes to read from bit stream.
     *
     * @return Number of successfully read bytes.
     */
    int read(final byte[] dest, final int offset, final int length);
}
